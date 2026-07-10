package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
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
class ObtenerRutaPorIdUseCaseTest {

  @Mock private AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;

  @Mock private ReservasCatalogoPort reservasCatalogoPort;

  @Test
  void obtieneRutaPorIdConInformacionDeMaterial() {
    UUID recicladorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    RutaRecoleccion ruta = ruta(recicladorId, publicacionId);
    when(almacenRutaRecoleccionPort.buscarPorId(ruta.id().valor())).thenReturn(Optional.of(ruta));
    when(reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId))
        .thenReturn(List.of(reserva(publicacionId)));
    ObtenerRutaPorIdUseCase useCase =
        new ObtenerRutaPorIdUseCase(almacenRutaRecoleccionPort, reservasCatalogoPort);

    Optional<RutaRecoleccionResultado> resultado = useCase.ejecutar(ruta.id().valor());

    assertTrue(resultado.isPresent());
    assertEquals(ruta.id().valor(), resultado.get().rutaId());
    assertEquals("CHATARRA", resultado.get().paradas().get(0).tipoResiduo());
    assertEquals(21.0, resultado.get().paradas().get(0).pesoKg());
  }

  @Test
  void retornaVacioSiNoExisteRuta() {
    UUID rutaId = UUID.randomUUID();
    when(almacenRutaRecoleccionPort.buscarPorId(rutaId)).thenReturn(Optional.empty());
    ObtenerRutaPorIdUseCase useCase =
        new ObtenerRutaPorIdUseCase(almacenRutaRecoleccionPort, reservasCatalogoPort);

    assertTrue(useCase.ejecutar(rutaId).isEmpty());
  }

  private RutaRecoleccion ruta(UUID recicladorId, UUID publicacionId) {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(recicladorId),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                new DestinoRecoleccion(
                    PublicacionId.de(publicacionId), new CoordenadaGPS(-0.1907, -78.4684))),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));
  }

  private ReservaCatalogo reserva(UUID publicacionId) {
    return new ReservaCatalogo(
        publicacionId,
        UUID.randomUUID(),
        "CHATARRA",
        21.0,
        -0.1907,
        -78.4684,
        Instant.parse("2026-07-09T14:00:00Z"));
  }
}
