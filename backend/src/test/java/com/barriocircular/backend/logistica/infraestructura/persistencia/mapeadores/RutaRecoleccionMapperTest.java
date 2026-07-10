package com.barriocircular.backend.logistica.infraestructura.persistencia.mapeadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.dominio.servicios.CalculadorDistanciaGeografica;
import com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.RutaRecoleccionEntity;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RutaRecoleccionMapperTest {

  private final RutaRecoleccionMapper mapper = new RutaRecoleccionMapper();

  @Test
  void convierteRutaDominioAEntidad() {
    RutaRecoleccion ruta = rutaPlanificada();
    Instant fechaCreacion = Instant.parse("2026-07-09T14:00:00Z");

    RutaRecoleccionEntity entity = mapper.toEntity(ruta, fechaCreacion);

    assertEquals(ruta.id().valor(), entity.getId());
    assertEquals(ruta.recicladorId().valor(), entity.getRecicladorId());
    assertEquals(ruta.fecha(), entity.getFecha());
    assertEquals(ruta.horaInicio(), entity.getHoraInicio());
    assertEquals(ruta.estado().name(), entity.getEstado());
    assertEquals(fechaCreacion, entity.getFechaCreacion());
    assertEquals(2, entity.getParadas().size());
    assertEquals(ruta.paradas().get(0).id().valor(), entity.getParadas().get(0).getId());
  }

  @Test
  void convierteEntidadADominioPreservandoIdentidadEstadoYHorarios() {
    RutaRecoleccion ruta = rutaPlanificada();
    ruta.iniciar();
    ruta.completarParada(
        ruta.paradas().get(0).id(), HorarioParada.de(ruta.fecha(), LocalTime.of(10, 15)));
    RutaRecoleccionEntity entity = mapper.toEntity(ruta, Instant.parse("2026-07-09T14:00:00Z"));

    RutaRecoleccion recuperada = mapper.toDomain(entity);

    assertEquals(ruta.id(), recuperada.id());
    assertEquals(ruta.recicladorId(), recuperada.recicladorId());
    assertEquals(ruta.estado(), recuperada.estado());
    assertEquals(ruta.paradas().size(), recuperada.paradas().size());
    assertEquals(ruta.paradas().get(0).id(), recuperada.paradas().get(0).id());
    assertEquals(ruta.paradas().get(0).estado(), recuperada.paradas().get(0).estado());
    assertNotNull(recuperada.paradas().get(0).horarioReal());
  }

  private RutaRecoleccion rutaPlanificada() {
    return new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica())
        .planificar(
            RecicladorId.de(UUID.randomUUID()),
            new CoordenadaGPS(-0.180653, -78.467838),
            List.of(
                new DestinoRecoleccion(
                    PublicacionId.de(UUID.randomUUID()), new CoordenadaGPS(-0.1907, -78.4684)),
                new DestinoRecoleccion(
                    PublicacionId.de(UUID.randomUUID()), new CoordenadaGPS(-0.2024, -78.4918))),
            LocalDate.of(2026, 7, 9),
            LocalTime.of(9, 0));
  }
}
