package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.publicacion.aplicacion.comandos.EliminarPublicacionCommand;
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

class EliminarPublicacionUseCaseTest {

  @Test
  void eliminaUnaPublicacionDisponibleDelCreadorComoSoftDelete() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    EliminarPublicacionUseCase casoUso =
        new EliminarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID creadorId = UUID.randomUUID();
    Publicacion publicacion = crearPublicacionDisponible(creadorId);
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(creadorId, true, false)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    casoUso.ejecutar(new EliminarPublicacionCommand(publicacion.id().valor()), "user_creador");

    assertEquals(EstadoPublicacion.CANCELADA, publicacion.estado());
  }

  @Test
  void fallaSiQuienLlamaNoEsElCreador() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    EliminarPublicacionUseCase casoUso =
        new EliminarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    Publicacion publicacion = crearPublicacionDisponible(UUID.randomUUID());
    UUID intrusoId = UUID.randomUUID();
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_intruso"))
        .thenReturn(Optional.of(new PerfilCapacidades(intrusoId, true, false)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    assertThrows(
        PerfilNoAutorizadoException.class,
        () ->
            casoUso.ejecutar(
                new EliminarPublicacionCommand(publicacion.id().valor()), "user_intruso"));
  }

  @Test
  void fallaSiLaPublicacionYaEstaReservada() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    EliminarPublicacionUseCase casoUso =
        new EliminarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID creadorId = UUID.randomUUID();
    Publicacion publicacion = crearPublicacionReservada(creadorId, UUID.randomUUID());
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(creadorId, true, false)));
    when(repositorio.buscarPorId(publicacion.id())).thenReturn(Optional.of(publicacion));

    assertThrows(
        EstadoInvalidoException.class,
        () ->
            casoUso.ejecutar(
                new EliminarPublicacionCommand(publicacion.id().valor()), "user_creador"));
  }

  @Test
  void fallaDeFormaControladaSiLaPublicacionNoExiste() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    EliminarPublicacionUseCase casoUso =
        new EliminarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    UUID clerkPerfilId = UUID.randomUUID();
    PublicacionId idInexistente = PublicacionId.nuevo();
    when(perfilConsultor.obtenerCapacidadesPorClerkId("user_creador"))
        .thenReturn(Optional.of(new PerfilCapacidades(clerkPerfilId, true, false)));
    when(repositorio.buscarPorId(idInexistente)).thenReturn(Optional.empty());

    assertThrows(
        PublicacionNoEncontradaException.class,
        () ->
            casoUso.ejecutar(
                new EliminarPublicacionCommand(idInexistente.valor()), "user_creador"));
  }

  @Test
  void fallaDeFormaControladaSiNoExistePerfil() {
    PublicacionRepositorio repositorio = mock(PublicacionRepositorio.class);
    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    PerfilConsultor perfilConsultor = mock(PerfilConsultor.class);
    EliminarPublicacionUseCase casoUso =
        new EliminarPublicacionUseCase(repositorio, eventPublisher, perfilConsultor);

    when(perfilConsultor.obtenerCapacidadesPorClerkId("desconocido")).thenReturn(Optional.empty());

    assertThrows(
        PerfilNoEncontradoException.class,
        () -> casoUso.ejecutar(new EliminarPublicacionCommand(UUID.randomUUID()), "desconocido"));
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

  private Publicacion crearPublicacionReservada(UUID creadorId, UUID reservadorId) {
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
