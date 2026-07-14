package com.barriocircular.backend.logistica.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.logistica.dominio.modelo.EstadoParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccionId;
import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccionId;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.ParadaRecoleccionEntity;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.RutaRecoleccionEntity;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class RutaRecoleccionMapper {

  public RutaRecoleccionEntity toEntity(RutaRecoleccion ruta) {
    return toEntity(ruta, Instant.now());
  }

  public RutaRecoleccionEntity toEntity(RutaRecoleccion ruta, Instant fechaCreacion) {
    Objects.requireNonNull(ruta, "La ruta de dominio es obligatoria.");
    Objects.requireNonNull(fechaCreacion, "La fecha de creacion es obligatoria.");
    List<ParadaRecoleccionEntity> paradas =
        ruta.paradas().stream().map(parada -> toEntity(ruta, parada)).toList();

    return new RutaRecoleccionEntity(
        ruta.id().valor(),
        ruta.recicladorId().valor(),
        ruta.fecha(),
        ruta.horaInicio(),
        ruta.estado().name(),
        fechaCreacion,
        paradas);
  }

  private ParadaRecoleccionEntity toEntity(RutaRecoleccion ruta, ParadaRecoleccion parada) {
    return new ParadaRecoleccionEntity(
        parada.id().valor(),
        ruta.id().valor(),
        parada.publicacionId().valor(),
        parada.orden(),
        parada.ubicacion().latitud(),
        parada.ubicacion().longitud(),
        parada.horarioEstimado().fechaHora(),
        parada.horarioReal() == null ? null : parada.horarioReal().fechaHora(),
        parada.estado().name());
  }

  public RutaRecoleccion toDomain(RutaRecoleccionEntity entity) {
    Objects.requireNonNull(entity, "La entidad de ruta es obligatoria.");
    List<ParadaRecoleccion> paradas = entity.getParadas().stream().map(this::toDomain).toList();

    return reconstruirRuta(entity, paradas);
  }

  private ParadaRecoleccion toDomain(ParadaRecoleccionEntity entity) {
    return reconstruirParada(entity);
  }

  private RutaRecoleccion reconstruirRuta(
      RutaRecoleccionEntity entity, List<ParadaRecoleccion> paradas) {
    try {
      Constructor<RutaRecoleccion> constructor =
          RutaRecoleccion.class.getDeclaredConstructor(
              RutaRecoleccionId.class,
              RecicladorId.class,
              java.time.LocalDate.class,
              java.time.LocalTime.class,
              EstadoRutaRecoleccion.class,
              List.class);
      constructor.setAccessible(true);
      return constructor.newInstance(
          RutaRecoleccionId.de(entity.getId()),
          RecicladorId.de(entity.getRecicladorId()),
          entity.getFecha(),
          entity.getHoraInicio(),
          EstadoRutaRecoleccion.valueOf(entity.getEstado()),
          paradas);
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException("No fue posible reconstruir la ruta de recoleccion.", ex);
    }
  }

  private ParadaRecoleccion reconstruirParada(ParadaRecoleccionEntity entity) {
    try {
      Constructor<ParadaRecoleccion> constructor =
          ParadaRecoleccion.class.getDeclaredConstructor(
              ParadaRecoleccionId.class,
              PublicacionId.class,
              CoordenadaGPS.class,
              int.class,
              HorarioParada.class,
              HorarioParada.class,
              EstadoParadaRecoleccion.class);
      constructor.setAccessible(true);
      return constructor.newInstance(
          ParadaRecoleccionId.de(entity.getId()),
          PublicacionId.de(entity.getPublicacionId()),
          new CoordenadaGPS(entity.getLatitud(), entity.getLongitud()),
          entity.getOrden(),
          new HorarioParada(entity.getHoraLlegadaEstimada()),
          entity.getHoraLlegadaReal() == null
              ? null
              : new HorarioParada(entity.getHoraLlegadaReal()),
          EstadoParadaRecoleccion.valueOf(entity.getEstado()));
    } catch (ReflectiveOperationException ex) {
      throw new IllegalStateException("No fue posible reconstruir la parada de recoleccion.", ex);
    }
  }
}
