package com.barriocircular.backend.logistica.infraestructura.persistencia.adaptadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.UUID;
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

  private RutaRecoleccion rutaPlanificada(UUID recicladorId, LocalDate fecha) {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(recicladorId),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                new DestinoRecoleccion(
                    PublicacionId.de(UUID.randomUUID()), new CoordenadaGPS(-0.1907, -78.4684)),
                new DestinoRecoleccion(
                    PublicacionId.de(UUID.randomUUID()), new CoordenadaGPS(-0.2024, -78.4918))),
            fecha,
            LocalTime.of(9, 0));
  }
}
