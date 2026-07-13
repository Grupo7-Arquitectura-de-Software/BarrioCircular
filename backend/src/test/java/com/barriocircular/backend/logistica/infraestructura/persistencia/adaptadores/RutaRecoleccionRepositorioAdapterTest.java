package com.barriocircular.backend.logistica.infraestructura.persistencia.adaptadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.dominio.servicios.CalculadorDistanciaGeografica;
import com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.SpringDataRutaRecoleccionRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@ActiveProfiles("test")
class RutaRecoleccionRepositorioAdapterTest {

  @Autowired private RutaRecoleccionRepositorioAdapter adapter;

  @Autowired private SpringDataRutaRecoleccionRepository springDataRepository;

  @AfterEach
  void limpiar() {
    springDataRepository.deleteAll();
  }

  @Test
  void guardaYRecuperaRutaPorId() {
    RutaRecoleccion ruta = rutaPlanificada(UUID.randomUUID(), LocalDate.of(2026, 7, 9));
    ruta.iniciar();
    ruta.completarParada(
        ruta.paradas().get(0).id(), HorarioParada.de(ruta.fecha(), LocalTime.of(10, 15)));

    RutaRecoleccion guardada = adapter.guardar(ruta);
    Optional<RutaRecoleccion> recuperada = adapter.buscarPorId(guardada.id().valor());

    assertTrue(recuperada.isPresent());
    assertEquals(guardada.id(), recuperada.get().id());
    assertEquals(guardada.estado(), recuperada.get().estado());
    assertEquals(2, recuperada.get().paradas().size());
    assertEquals("COMPLETADA", recuperada.get().paradas().get(0).estado().name());
  }

  @Test
  void obtieneRutaActivaPorRecicladorEnFechaActual() {
    UUID recicladorId = UUID.randomUUID();
    LocalDate fechaActual = LocalDate.now(HorarioParada.ZONA_OPERATIVA);
    RutaRecoleccion rutaActual = rutaPlanificada(recicladorId, fechaActual);
    RutaRecoleccion rutaOtraFecha = rutaPlanificada(recicladorId, fechaActual.minusDays(1));

    adapter.guardar(rutaOtraFecha);
    adapter.guardar(rutaActual);

    Optional<RutaRecoleccion> activa = adapter.obtenerRutaActivaPorReciclador(recicladorId);

    assertTrue(activa.isPresent());
    assertEquals(rutaActual.id(), activa.get().id());
  }

  @Test
  void conservaZonaOperativaEnHorariosDespuesDePersistir() {
    RutaRecoleccion ruta = rutaPlanificada(UUID.randomUUID(), LocalDate.of(2026, 7, 9));
    ruta.iniciar();
    ruta.completarParada(
        ruta.paradas().get(0).id(), HorarioParada.de(ruta.fecha(), LocalTime.of(10, 15)));

    RutaRecoleccion guardada = adapter.guardar(ruta);
    RutaRecoleccion recuperada = adapter.buscarPorId(guardada.id().valor()).orElseThrow();

    assertEquals(
        HorarioParada.ZONA_OPERATIVA,
        recuperada.paradas().get(0).horarioEstimado().fechaHora().getZone());
    assertEquals(
        HorarioParada.ZONA_OPERATIVA,
        recuperada.paradas().get(0).horarioReal().fechaHora().getZone());
  }

  @Test
  void sincronizaParadasAlGuardarRutaExistenteReplanificada() {
    UUID recicladorId = UUID.randomUUID();
    LocalDate fecha = LocalDate.of(2026, 7, 9);
    UUID publicacionRetirada = UUID.randomUUID();
    UUID publicacionUno = UUID.randomUUID();
    UUID publicacionDos = UUID.randomUUID();
    RutaRecoleccion rutaInicial =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(destino(publicacionRetirada, -0.1907, -78.4684)));
    RutaRecoleccion guardada = adapter.guardar(rutaInicial);

    RutaRecoleccion recargada = adapter.buscarPorId(guardada.id().valor()).orElseThrow();
    RutaRecoleccion rutaReplanificada =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(
                destino(publicacionUno, -0.2024, -78.4918),
                destino(publicacionDos, -0.215, -78.5)));

    recargada.replanificar(rutaReplanificada.paradas());
    RutaRecoleccion actualizada = adapter.guardar(recargada);
    RutaRecoleccion recuperada = adapter.buscarPorId(actualizada.id().valor()).orElseThrow();

    assertEquals(guardada.id(), actualizada.id());
    assertEquals(1, springDataRepository.count());
    assertEquals(2, recuperada.paradas().size());
    assertEquals(publicacionUno, recuperada.paradas().get(0).publicacionId().valor());
    assertEquals(publicacionDos, recuperada.paradas().get(1).publicacionId().valor());
    assertEquals(1, recuperada.paradas().get(0).orden());
    assertEquals(2, recuperada.paradas().get(1).orden());

    Set<UUID> publicacionesPersistidas =
        recuperada.paradas().stream()
            .map(parada -> parada.publicacionId().valor())
            .collect(Collectors.toSet());
    assertFalse(publicacionesPersistidas.contains(publicacionRetirada));
  }

  @Test
  void eliminaParadasRetiradasAlReplanificarRutaExistente() {
    UUID recicladorId = UUID.randomUUID();
    LocalDate fecha = LocalDate.of(2026, 7, 9);
    UUID publicacionConservada = UUID.randomUUID();
    UUID publicacionRetirada = UUID.randomUUID();
    RutaRecoleccion rutaInicial =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(
                destino(publicacionConservada, -0.1907, -78.4684),
                destino(publicacionRetirada, -0.2024, -78.4918)));
    RutaRecoleccion guardada = adapter.guardar(rutaInicial);

    RutaRecoleccion recargada = adapter.buscarPorId(guardada.id().valor()).orElseThrow();
    RutaRecoleccion rutaReplanificada =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(destino(publicacionConservada, -0.1907, -78.4684)));

    recargada.replanificar(rutaReplanificada.paradas());
    adapter.guardar(recargada);
    RutaRecoleccion recuperada = adapter.buscarPorId(guardada.id().valor()).orElseThrow();

    assertEquals(1, recuperada.paradas().size());
    assertEquals(publicacionConservada, recuperada.paradas().get(0).publicacionId().valor());
    assertFalse(
        recuperada.paradas().stream()
            .anyMatch(parada -> parada.publicacionId().valor().equals(publicacionRetirada)));
  }

  @Test
  void actualizarDosVecesNoDuplicaParadas() {
    UUID recicladorId = UUID.randomUUID();
    LocalDate fecha = LocalDate.of(2026, 7, 9);
    UUID publicacionUno = UUID.randomUUID();
    UUID publicacionDos = UUID.randomUUID();
    RutaRecoleccion rutaInicial =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(destino(publicacionUno, -0.1907, -78.4684)));
    RutaRecoleccion guardada = adapter.guardar(rutaInicial);
    RutaRecoleccion rutaReplanificada =
        rutaPlanificada(
            recicladorId,
            fecha,
            List.of(
                destino(publicacionUno, -0.1907, -78.4684),
                destino(publicacionDos, -0.2024, -78.4918)));

    RutaRecoleccion primeraActualizacion = adapter.buscarPorId(guardada.id().valor()).orElseThrow();
    primeraActualizacion.replanificar(rutaReplanificada.paradas());
    adapter.guardar(primeraActualizacion);

    RutaRecoleccion segundaActualizacion = adapter.buscarPorId(guardada.id().valor()).orElseThrow();
    segundaActualizacion.replanificar(rutaReplanificada.paradas());
    adapter.guardar(segundaActualizacion);

    RutaRecoleccion recuperada = adapter.buscarPorId(guardada.id().valor()).orElseThrow();
    assertEquals(2, recuperada.paradas().size());
    assertEquals(
        2,
        recuperada.paradas().stream().map(parada -> parada.publicacionId().valor()).distinct()
            .count());
  }

  private RutaRecoleccion rutaPlanificada(UUID recicladorId, LocalDate fecha) {
    return rutaPlanificada(
        recicladorId,
        fecha,
        List.of(
            destino(UUID.randomUUID(), -0.1907, -78.4684),
            destino(UUID.randomUUID(), -0.2024, -78.4918)));
  }

  private RutaRecoleccion rutaPlanificada(
      UUID recicladorId, LocalDate fecha, List<DestinoRecoleccion> destinos) {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(recicladorId),
            new CoordenadaGPS(-0.180653, -78.467838),
            destinos,
            fecha,
            LocalTime.of(9, 0));
  }

  private DestinoRecoleccion destino(UUID publicacionId, double latitud, double longitud) {
    return new DestinoRecoleccion(
        PublicacionId.de(publicacionId), new CoordenadaGPS(latitud, longitud));
  }
}
