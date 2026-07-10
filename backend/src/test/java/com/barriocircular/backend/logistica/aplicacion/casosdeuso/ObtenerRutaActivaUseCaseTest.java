package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
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
class ObtenerRutaActivaUseCaseTest {

  @Mock private AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;

  @Mock private ReservasCatalogoPort reservasCatalogoPort;

  @Mock private UbicacionRecicladorPort ubicacionRecicladorPort;

  @Test
  void obtieneRutaActivaDelRecicladorComoDto() {
    UUID recicladorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    RutaRecoleccion ruta = ruta(recicladorId, publicacionId);
    when(almacenRutaRecoleccionPort.obtenerRutaActivaPorReciclador(recicladorId))
        .thenReturn(Optional.of(ruta));
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId))
        .thenReturn(List.of(reserva(publicacionId)));
    when(ubicacionRecicladorPort.obtenerUbicacionActual(recicladorId))
        .thenReturn(Optional.of(new CoordenadaGPS(-0.180653, -78.467838)));
    ObtenerRutaActivaUseCase useCase =
        new ObtenerRutaActivaUseCase(
            almacenRutaRecoleccionPort, reservasCatalogoPort, ubicacionRecicladorPort);

    Optional<RutaRecoleccionResultado> resultado = useCase.ejecutar(recicladorId);

    assertTrue(resultado.isPresent());
    assertEquals(ruta.id().valor(), resultado.get().rutaId());
    assertEquals("PLANIFICADA", resultado.get().estado());
    assertEquals(-0.180653, resultado.get().origen().latitud());
    assertEquals(-78.467838, resultado.get().origen().longitud());
    assertEquals("VIDRIO", resultado.get().paradas().get(0).tipoResiduo());
    assertEquals(8.5, resultado.get().paradas().get(0).pesoKg());
  }

  @Test
  void retornaVacioSiNoExisteRutaActiva() {
    UUID recicladorId = UUID.randomUUID();
    when(almacenRutaRecoleccionPort.obtenerRutaActivaPorReciclador(recicladorId))
        .thenReturn(Optional.empty());
    ObtenerRutaActivaUseCase useCase =
        new ObtenerRutaActivaUseCase(
            almacenRutaRecoleccionPort, reservasCatalogoPort, ubicacionRecicladorPort);

    assertTrue(useCase.ejecutar(recicladorId).isEmpty());
  }

  private RutaRecoleccion ruta(UUID recicladorId, UUID publicacionId) {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(recicladorId),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                new DestinoRecoleccion(
                    com.barriocircular.backend.logistica.dominio.modelo.PublicacionId.de(
                        publicacionId),
                    new CoordenadaGPS(-0.1907, -78.4684))),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));
  }

  private ReservaCatalogo reserva(UUID publicacionId) {
    return new ReservaCatalogo(
        publicacionId,
        UUID.randomUUID(),
        "VIDRIO",
        8.5,
        -0.1907,
        -78.4684,
        Instant.parse("2026-07-09T14:00:00Z"));
  }
}
