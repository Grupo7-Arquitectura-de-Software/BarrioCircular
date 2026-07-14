package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.servicios.CalculadorDistanciaGeografica;
import com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import java.time.Instant;
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
class RegistrarLlegadaParadaUseCaseTest {

  @Mock private AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;

  @Mock private ReservasCatalogoPort reservasCatalogoPort;

  @Mock private UbicacionRecicladorPort ubicacionRecicladorPort;

  @Test
  void registraLlegadaIniciandoLaParadaMedianteElAgregado() {
    RutaRecoleccion ruta = rutaEnCurso();
    UUID paradaId = ruta.paradas().get(0).id().valor();
    UUID publicacionId = ruta.paradas().get(0).publicacionId().valor();
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    when(almacenRutaRecoleccionPort.guardar(any(RutaRecoleccion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(ruta.recicladorId().valor()))
        .thenReturn(List.of(reserva(publicacionId)));
    when(ubicacionRecicladorPort.obtenerUbicacionActual(ruta.recicladorId().valor()))
        .thenReturn(Optional.of(new CoordenadaGPS(-0.180653, -78.467838)));
    RegistrarLlegadaParadaUseCase useCase =
        new RegistrarLlegadaParadaUseCase(
            almacenRutaRecoleccionPort, reservasCatalogoPort, ubicacionRecicladorPort);

    RutaRecoleccionResultado resultado =
        useCase.ejecutar(
            ruta.id().valor(), paradaId, LocalDate.of(2026, 7, 9), LocalTime.of(10, 15));

    assertEquals("EN_PROGRESO", resultado.paradas().get(0).estado());
    assertEquals(-0.180653, resultado.origen().latitud());
    assertEquals(-78.467838, resultado.origen().longitud());
    assertEquals("PET", resultado.paradas().get(0).tipoResiduo());
    assertEquals(14.2, resultado.paradas().get(0).pesoKg());
    assertNotNull(resultado.paradas().get(0).horaLlegadaReal());
    verify(almacenRutaRecoleccionPort).guardar(ruta);
  }

  @Test
  void respetaReglasDelDominioSiLaRutaNoEstaEnCurso() {
    RutaRecoleccion ruta = rutaPlanificada();
    UUID paradaId = ruta.paradas().get(0).id().valor();
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    RegistrarLlegadaParadaUseCase useCase =
        new RegistrarLlegadaParadaUseCase(
            almacenRutaRecoleccionPort, reservasCatalogoPort, ubicacionRecicladorPort);

    assertThrows(
        IllegalStateException.class,
        () ->
            useCase.ejecutar(
                ruta.id().valor(), paradaId, LocalDate.of(2026, 7, 9), LocalTime.of(10, 15)));
  }

  private RutaRecoleccion rutaEnCurso() {
    RutaRecoleccion ruta = rutaPlanificada();
    ruta.iniciar();
    return ruta;
  }

  private RutaRecoleccion rutaPlanificada() {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(UUID.randomUUID()),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                new DestinoRecoleccion(
                    PublicacionId.de(UUID.randomUUID()), new CoordenadaGPS(-0.1907, -78.4684))),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));
  }

  private ReservaCatalogo reserva(UUID publicacionId) {
    return new ReservaCatalogo(
        publicacionId,
        UUID.randomUUID(),
        "PET",
        14.2,
        -0.1907,
        -78.4684,
        Instant.parse("2026-07-09T14:00:00Z"));
  }
}
