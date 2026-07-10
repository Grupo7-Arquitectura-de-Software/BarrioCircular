package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
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
class ConstruirRutaRecoleccionUseCaseTest {

  @Mock private ReservasCatalogoPort reservasCatalogoPort;

  @Mock private UbicacionRecicladorPort ubicacionRecicladorPort;

  @Mock private AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;

  @Test
  void construyeRutaConReservasActivasYUbicacionDelReciclador() {
    UUID recicladorId = UUID.randomUUID();
    ReservaCatalogo reservaCercana =
        reserva("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684);
    ReservaCatalogo reservaLejana =
        reserva("22222222-2222-2222-2222-222222222222", -0.2201, -78.5123);
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId))
        .thenReturn(List.of(reservaLejana, reservaCercana));
    when(ubicacionRecicladorPort.obtenerUbicacionActual(recicladorId))
        .thenReturn(Optional.of(new CoordenadaGPS(-0.180653, -78.467838)));
    when(almacenRutaRecoleccionPort.guardar(any(RutaRecoleccion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    ConstruirRutaRecoleccionUseCase useCase =
        new ConstruirRutaRecoleccionUseCase(
            reservasCatalogoPort, ubicacionRecicladorPort, almacenRutaRecoleccionPort);

    RutaRecoleccionResultado resultado =
        useCase.ejecutar(recicladorId, LocalTime.of(9, 0), LocalDate.of(2026, 7, 9));

    assertEquals("PLANIFICADA", resultado.estado());
    assertEquals(LocalDate.of(2026, 7, 9), resultado.fecha());
    assertEquals(2, resultado.paradas().size());
    assertEquals(reservaCercana.publicacionId(), resultado.paradas().get(0).publicacionId());
    assertEquals("CARTON", resultado.paradas().get(0).tipoResiduo());
    assertEquals(10.0, resultado.paradas().get(0).pesoKg());
    assertEquals(1, resultado.paradas().get(0).orden());
    verify(almacenRutaRecoleccionPort).guardar(any(RutaRecoleccion.class));
  }

  @Test
  void rechazaConstruirRutaSinReservasActivas() {
    UUID recicladorId = UUID.randomUUID();
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId))
        .thenReturn(List.of());
    ConstruirRutaRecoleccionUseCase useCase =
        new ConstruirRutaRecoleccionUseCase(
            reservasCatalogoPort, ubicacionRecicladorPort, almacenRutaRecoleccionPort);

    IllegalStateException error =
        assertThrows(
            IllegalStateException.class,
            () -> useCase.ejecutar(recicladorId, LocalTime.of(9, 0), LocalDate.of(2026, 7, 9)));

    assertTrue(error.getMessage().contains("reservas activas"));
  }

  @Test
  void rechazaConstruirRutaSinUbicacionDelReciclador() {
    UUID recicladorId = UUID.randomUUID();
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId))
        .thenReturn(List.of(reserva("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684)));
    when(ubicacionRecicladorPort.obtenerUbicacionActual(recicladorId)).thenReturn(Optional.empty());
    ConstruirRutaRecoleccionUseCase useCase =
        new ConstruirRutaRecoleccionUseCase(
            reservasCatalogoPort, ubicacionRecicladorPort, almacenRutaRecoleccionPort);

    IllegalStateException error =
        assertThrows(
            IllegalStateException.class,
            () -> useCase.ejecutar(recicladorId, LocalTime.of(9, 0), LocalDate.of(2026, 7, 9)));

    assertTrue(error.getMessage().contains("ubicacion actual"));
  }

  private ReservaCatalogo reserva(String publicacionId, double latitud, double longitud) {
    return new ReservaCatalogo(
        UUID.fromString(publicacionId),
        UUID.randomUUID(),
        "CARTON",
        10.0,
        latitud,
        longitud,
        Instant.parse("2026-07-09T14:00:00Z"));
  }
}
