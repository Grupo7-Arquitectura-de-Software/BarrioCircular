package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionPublicacionResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ConfirmacionPublicacionPort;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.dominio.servicios.CalculadorDistanciaGeografica;
import com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfirmarRecoleccionUseCaseTest {

  @Mock private AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;

  @Mock private ConfirmacionPublicacionPort confirmacionPublicacionPort;

  @Test
  void confirmaPublicacionYCompletaParadaManteniendoRutaEnCursoSiQuedanPendientes() {
    UUID recolectorId = UUID.randomUUID();
    RutaRecoleccion ruta = rutaEnCursoConParadaEnProgreso(recolectorId, 2);
    UUID paradaId = ruta.paradas().get(0).id().valor();
    UUID publicacionId = ruta.paradas().get(0).publicacionId().valor();
    prepararConfirmacion(ruta, recolectorId, publicacionId, 10.5, "Material humedo.");
    ConfirmarRecoleccionUseCase useCase =
        new ConfirmarRecoleccionUseCase(almacenRutaRecoleccionPort, confirmacionPublicacionPort);

    ConfirmacionRecoleccionResultado resultado =
        useCase.ejecutar(recolectorId, ruta.id().valor(), paradaId, 10.5, "Material humedo.");

    assertEquals("EN_CURSO", resultado.estadoRuta());
    assertEquals("COMPLETADA", resultado.estadoParada());
    assertEquals("FINALIZADA", resultado.estadoPublicacion());
    assertEquals(10.5, resultado.pesoRealVerificado());
    assertEquals(EstadoRutaRecoleccion.EN_CURSO, ruta.estado());
    assertEquals(EstadoParadaRecoleccion.COMPLETADA, ruta.paradas().get(0).estado());
    verify(almacenRutaRecoleccionPort).guardar(ruta);
  }

  @Test
  void completaRutaAutomaticamenteSiEraLaUltimaParada() {
    UUID recolectorId = UUID.randomUUID();
    RutaRecoleccion ruta = rutaEnCursoConParadaEnProgreso(recolectorId, 1);
    UUID paradaId = ruta.paradas().get(0).id().valor();
    UUID publicacionId = ruta.paradas().get(0).publicacionId().valor();
    prepararConfirmacion(ruta, recolectorId, publicacionId, 9.8, null);
    ConfirmarRecoleccionUseCase useCase =
        new ConfirmarRecoleccionUseCase(almacenRutaRecoleccionPort, confirmacionPublicacionPort);

    ConfirmacionRecoleccionResultado resultado =
        useCase.ejecutar(recolectorId, ruta.id().valor(), paradaId, 9.8, null);

    assertEquals("COMPLETADA", resultado.estadoRuta());
    assertEquals(EstadoRutaRecoleccion.COMPLETADA, ruta.estado());
  }

  @Test
  void rechazaPesoRealInvalidoAntesDeConfirmarPublicacion() {
    RutaRecoleccion ruta = rutaEnCursoConParadaEnProgreso(UUID.randomUUID(), 1);
    UUID paradaId = ruta.paradas().get(0).id().valor();
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    ConfirmarRecoleccionUseCase useCase =
        new ConfirmarRecoleccionUseCase(almacenRutaRecoleccionPort, confirmacionPublicacionPort);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            useCase.ejecutar(ruta.recicladorId().valor(), ruta.id().valor(), paradaId, 0.0, null));

    verify(confirmacionPublicacionPort, never())
        .confirmarRecoleccion(any(), any(), any(Double.class), any());
  }

  @Test
  void rechazaRutaDeOtroRecolector() {
    UUID recolectorId = UUID.randomUUID();
    RutaRecoleccion ruta = rutaEnCursoConParadaEnProgreso(UUID.randomUUID(), 1);
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    ConfirmarRecoleccionUseCase useCase =
        new ConfirmarRecoleccionUseCase(almacenRutaRecoleccionPort, confirmacionPublicacionPort);

    assertThrows(
        IllegalStateException.class,
        () ->
            useCase.ejecutar(
                recolectorId, ruta.id().valor(), ruta.paradas().get(0).id().valor(), 1.0, null));
  }

  @Test
  void rechazaParadaPendiente() {
    UUID recolectorId = UUID.randomUUID();
    RutaRecoleccion ruta = rutaEnCurso(recolectorId, 1);
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    ConfirmarRecoleccionUseCase useCase =
        new ConfirmarRecoleccionUseCase(almacenRutaRecoleccionPort, confirmacionPublicacionPort);

    assertThrows(
        IllegalStateException.class,
        () ->
            useCase.ejecutar(
                recolectorId, ruta.id().valor(), ruta.paradas().get(0).id().valor(), 1.0, null));
  }

  private void prepararConfirmacion(
      RutaRecoleccion ruta,
      UUID recolectorId,
      UUID publicacionId,
      double pesoRealVerificado,
      String observaciones) {
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    when(confirmacionPublicacionPort.confirmarRecoleccion(
            publicacionId, recolectorId, pesoRealVerificado, observaciones))
        .thenReturn(
            new ConfirmacionPublicacionResultado(
                publicacionId, "FINALIZADA", pesoRealVerificado, observaciones));
    when(almacenRutaRecoleccionPort.guardar(any(RutaRecoleccion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private RutaRecoleccion rutaEnCursoConParadaEnProgreso(UUID recolectorId, int cantidadParadas) {
    RutaRecoleccion ruta = rutaEnCurso(recolectorId, cantidadParadas);
    ruta.iniciarParada(
        ruta.paradas().get(0).id(), HorarioParada.de(LocalDate.of(2026, 7, 9), LocalTime.NOON));
    return ruta;
  }

  private RutaRecoleccion rutaEnCurso(UUID recolectorId, int cantidadParadas) {
    List<DestinoRecoleccion> destinos =
        java.util.stream.IntStream.range(0, cantidadParadas)
            .mapToObj(
                indice ->
                    new DestinoRecoleccion(
                        PublicacionId.de(UUID.randomUUID()),
                        new CoordenadaGPS(-0.1907 - (indice * 0.01), -78.4684)))
            .toList();
    RutaRecoleccion ruta =
        new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
            .planificar(
                RecicladorId.de(recolectorId),
                new CoordenadaGPS(-0.180653, -78.467838),
                destinos,
                LocalDate.of(2026, 7, 9),
                LocalTime.of(9, 0));
    ruta.iniciar();
    return ruta;
  }
}
