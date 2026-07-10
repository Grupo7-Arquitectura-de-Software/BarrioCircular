package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.publicacion.aplicacion.comandos.FinalizarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;
import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.DetalleMaterial;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.EvidenciaVisual;
import com.barriocircular.backend.publicacion.dominio.modelo.PesoEstimado;
import com.barriocircular.backend.publicacion.dominio.modelo.PrecioPorKilo;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.modelo.UbicacionRecogida;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class FinalizarPublicacionUseCaseTest {

  @Test
  void finalizaUnaPublicacionReservadaCuandoQuienLlamaEsElReservador() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID reservadorId = UUID.randomUUID();
    Publicacion publicacion = crearPublicacionReservadaPor(reservadorId);
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_reservador"))
        .thenReturn(Optional.of(new PerfilCapacidades(reservadorId, false, true)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    var resultado =
        casoUso.ejecutar(
            new FinalizarPublicacionCommand(publicacion.id().valor()), "user_reservador");

    assertEquals("FINALIZADA", resultado.estado());
  }

  @Test
  void finalizaUnaPublicacionReservadaCuandoQuienLlamaEsElCreador() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID creadorId = UUID.randomUUID();
    Publicacion publicacion = crearPublicacionReservadaConCreador(creadorId, UUID.randomUUID());
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(creadorId, true, false)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    var resultado =
        casoUso.ejecutar(new FinalizarPublicacionCommand(publicacion.id().valor()), "user_creador");

    assertEquals("FINALIZADA", resultado.estado());
  }

  @Test
  void fallaSiQuienLlamaNoEsNiCreadorNiReservador() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    Publicacion publicacion = crearPublicacionReservadaPor(UUID.randomUUID());
    UUID intrusoId = UUID.randomUUID();
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_intruso"))
        .thenReturn(Optional.of(new PerfilCapacidades(intrusoId, false, true)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    assertThrows(
        PerfilNoAutorizadoException.class,
        () ->
            casoUso.ejecutar(
                new FinalizarPublicacionCommand(publicacion.id().valor()), "user_intruso"));
  }

  @Test
  void fallaSiLaPublicacionAunEstaDisponible() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID creadorId = UUID.randomUUID();
    Publicacion publicacion = crearPublicacionDisponible(creadorId);
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(creadorId, true, false)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    assertThrows(
        EstadoInvalidoException.class,
        () ->
            casoUso.ejecutar(
                new FinalizarPublicacionCommand(publicacion.id().valor()), "user_creador"));
  }

  @Test
  void fallaDeFormaControladaSiLaPublicacionNoExiste() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID clerkPerfilId = UUID.randomUUID();
    PublicacionId idInexistente = PublicacionId.nuevo();
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(clerkPerfilId, true, false)));
    when(repositorio.buscarPorId(idInexistente)).thenReturn(Optional.empty());

    assertThrows(
        PublicacionNoEncontradaException.class,
        () ->
            casoUso.ejecutar(
                new FinalizarPublicacionCommand(idInexistente.valor()), "user_creador"));
  }

  @Test
  void fallaDeFormaControladaSiNoExistePerfil() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    FinalizarPublicacionUseCase casoUso =
        new FinalizarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    when(perfilConsultor.obtenerCapacidadesPorClerkId("desconocido")).thenReturn(Optional.empty());

    assertThrows(
        PerfilNoEncontradoException.class,
        () -> casoUso.ejecutar(new FinalizarPublicacionCommand(UUID.randomUUID()), "desconocido"));
  }

  private Publicacion crearPublicacionDisponible(UUID creadorId) {
    return Publicacion.reconstituir(
        PublicacionId.nuevo(),
        CiudadanoId.de(creadorId),
        new DetalleMaterial(TipoResiduo.PET, new PesoEstimado(10)),
        new PrecioPorKilo(new BigDecimal("1.50")),
        new UbicacionRecogida(-0.18, -78.47),
        new EvidenciaVisual("https://ejemplo.com/foto.jpg"),
        Instant.now(),
        EstadoPublicacion.DISPONIBLE,
        null);
  }

  private Publicacion crearPublicacionReservadaPor(UUID reservadorId) {
    return crearPublicacionReservadaConCreador(UUID.randomUUID(), reservadorId);
  }

  private Publicacion crearPublicacionReservadaConCreador(UUID creadorId, UUID reservadorId) {
    return Publicacion.reconstituir(
        PublicacionId.nuevo(),
        CiudadanoId.de(creadorId),
        new DetalleMaterial(TipoResiduo.PET, new PesoEstimado(10)),
        new PrecioPorKilo(new BigDecimal("1.50")),
        new UbicacionRecogida(-0.18, -78.47),
        new EvidenciaVisual("https://ejemplo.com/foto.jpg"),
        Instant.now(),
        EstadoPublicacion.RESERVADA,
        ReservadorId.de(reservadorId));
  }
}
