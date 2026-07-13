package com.barriocircular.backend.logistica.dominio.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.logistica.dominio.modelo.EstadoParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PlanificadorRutaRecoleccionTest {

  private final PlanificadorRutaRecoleccion planificador =
      new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica());

  @Test
  void planificaRutaConTresParadasEnQuitoOrdenadasPorVecinoMasCercano() {
    CoordenadaGPS origenRecolector = new CoordenadaGPS(-0.180653, -78.467838);
    DestinoRecoleccion mariscal =
        destino("11111111-1111-1111-1111-111111111111", -0.2024, -78.4918);
    DestinoRecoleccion centroHistorico =
        destino("22222222-2222-2222-2222-222222222222", -0.2201, -78.5123);
    DestinoRecoleccion guapulo = destino("33333333-3333-3333-3333-333333333333", -0.1907, -78.4684);

    RutaRecoleccion ruta =
        planificador.planificar(
            RecicladorId.de(UUID.randomUUID()),
            origenRecolector,
            List.of(centroHistorico, mariscal, guapulo),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));

    assertNotNull(ruta.id());
    assertEquals(EstadoRutaRecoleccion.PLANIFICADA, ruta.estado());
    assertEquals(3, ruta.paradas().size());
    assertEquals(guapulo.publicacionId(), ruta.paradas().get(0).publicacionId());
    assertEquals(mariscal.publicacionId(), ruta.paradas().get(1).publicacionId());
    assertEquals(centroHistorico.publicacionId(), ruta.paradas().get(2).publicacionId());
  }

  @Test
  void calculaHorariosEstimadosAcumulandoViajeYServicioPorParada() {
    CoordenadaGPS origenRecolector = new CoordenadaGPS(-0.180653, -78.467838);
    List<DestinoRecoleccion> destinos =
        List.of(
            destino("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684),
            destino("22222222-2222-2222-2222-222222222222", -0.2024, -78.4918),
            destino("33333333-3333-3333-3333-333333333333", -0.2201, -78.5123));

    RutaRecoleccion ruta =
        planificador.planificar(
            RecicladorId.de(UUID.randomUUID()),
            origenRecolector,
            destinos,
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));

    HorarioParada primerHorario = ruta.paradas().get(0).horarioEstimado();
    HorarioParada segundoHorario = ruta.paradas().get(1).horarioEstimado();
    HorarioParada tercerHorario = ruta.paradas().get(2).horarioEstimado();

    assertTrue(
        primerHorario
            .fechaHora()
            .isAfter(HorarioParada.de(ruta.fecha(), ruta.horaInicio()).fechaHora()));
    assertTrue(segundoHorario.fechaHora().isAfter(primerHorario.fechaHora()));
    assertTrue(tercerHorario.fechaHora().isAfter(segundoHorario.fechaHora()));
    assertEquals(HorarioParada.ZONA_OPERATIVA, primerHorario.fechaHora().getZone());
  }

  @Test
  void creaParadasPendientesConOrdenConsecutivo() {
    RutaRecoleccion ruta =
        planificador.planificar(
            RecicladorId.de(UUID.randomUUID()),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                destino("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684),
                destino("22222222-2222-2222-2222-222222222222", -0.2024, -78.4918),
                destino("33333333-3333-3333-3333-333333333333", -0.2201, -78.5123)),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));

    assertEquals(1, ruta.paradas().get(0).orden());
    assertEquals(2, ruta.paradas().get(1).orden());
    assertEquals(3, ruta.paradas().get(2).orden());
    assertTrue(
        ruta.paradas().stream()
            .allMatch(parada -> parada.estado() == EstadoParadaRecoleccion.PENDIENTE));
  }

  @Test
  void rechazaCompletarRutaEnCursoConParadasPendientes() {
    RutaRecoleccion ruta =
        planificador.planificar(
            RecicladorId.de(UUID.randomUUID()),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                destino("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684),
                destino("22222222-2222-2222-2222-222222222222", -0.2024, -78.4918),
                destino("33333333-3333-3333-3333-333333333333", -0.2201, -78.5123)),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));

    ruta.iniciar();

    assertThrows(IllegalStateException.class, ruta::completar);
  }

  @Test
  void rechazaModificarParadasSiLaRutaNoEstaEnCurso() {
    RutaRecoleccion ruta =
        planificador.planificar(
            RecicladorId.de(UUID.randomUUID()),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(destino("11111111-1111-1111-1111-111111111111", -0.1907, -78.4684)),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));

    assertThrows(
        IllegalStateException.class,
        () ->
            ruta.iniciarParada(
                ruta.paradas().get(0).id(), HorarioParada.de(ruta.fecha(), ruta.horaInicio())));
  }

  @Test
  void rechazaPlanificarRutaSinDestinos() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            planificador.planificar(
                RecicladorId.de(UUID.randomUUID()),
                new CoordenadaGPS(-0.180653, -78.467838),
                List.of(),
                LocalDate.of(2026, 7, 9),
                LocalTime.of(9, 0)));
  }

  @Test
  void rechazaDestinosDuplicadosParaLaMismaPublicacion() {
    PublicacionId publicacionId =
        PublicacionId.de(UUID.fromString("11111111-1111-1111-1111-111111111111"));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            planificador.planificar(
                RecicladorId.de(UUID.randomUUID()),
                new CoordenadaGPS(-0.180653, -78.467838),
                List.of(
                    new DestinoRecoleccion(publicacionId, new CoordenadaGPS(-0.1907, -78.4684)),
                    new DestinoRecoleccion(publicacionId, new CoordenadaGPS(-0.2024, -78.4918))),
                LocalDate.of(2026, 7, 9),
                LocalTime.of(9, 0)));
  }

  private DestinoRecoleccion destino(String publicacionId, double latitud, double longitud) {
    return new DestinoRecoleccion(
        PublicacionId.de(UUID.fromString(publicacionId)), new CoordenadaGPS(latitud, longitud));
  }
}
