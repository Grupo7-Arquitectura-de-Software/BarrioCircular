package com.barriocircular.backend.logistica.aplicacion.dto;

import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccion;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public record ParadaRecoleccionResultado(
    UUID paradaId,
    UUID publicacionId,
    String tipoResiduo,
    Double pesoKg,
    String estado,
    int orden,
    ZonedDateTime horaLlegadaEstimada,
    ZonedDateTime horaLlegadaReal,
    double latitud,
    double longitud) {

  public ParadaRecoleccionResultado(
      UUID paradaId,
      UUID publicacionId,
      String estado,
      int orden,
      ZonedDateTime horaLlegadaEstimada,
      ZonedDateTime horaLlegadaReal,
      double latitud,
      double longitud) {
    this(
        paradaId,
        publicacionId,
        null,
        null,
        estado,
        orden,
        horaLlegadaEstimada,
        horaLlegadaReal,
        latitud,
        longitud);
  }

  public ParadaRecoleccionResultado {
    Objects.requireNonNull(paradaId, "El id de la parada es obligatorio.");
    Objects.requireNonNull(publicacionId, "El id de la publicacion es obligatorio.");
    if (tipoResiduo != null && tipoResiduo.isBlank()) {
      throw new IllegalArgumentException("El tipo de residuo no puede estar vacio.");
    }
    if (pesoKg != null && (!Double.isFinite(pesoKg) || pesoKg <= 0)) {
      throw new IllegalArgumentException("El peso de la parada debe ser mayor que cero.");
    }
    if (estado == null || estado.isBlank()) {
      throw new IllegalArgumentException("El estado de la parada es obligatorio.");
    }
    if (orden < 1) {
      throw new IllegalArgumentException("El orden de la parada debe iniciar en 1.");
    }
    Objects.requireNonNull(horaLlegadaEstimada, "La hora estimada de llegada es obligatoria.");
  }

  public static ParadaRecoleccionResultado desde(ParadaRecoleccion parada) {
    Objects.requireNonNull(parada, "La parada a convertir es obligatoria.");
    return new ParadaRecoleccionResultado(
        parada.id().valor(),
        parada.publicacionId().valor(),
        parada.estado().name(),
        parada.orden(),
        parada.horarioEstimado().fechaHora(),
        parada.horarioReal() == null ? null : parada.horarioReal().fechaHora(),
        parada.ubicacion().latitud(),
        parada.ubicacion().longitud());
  }

  public static ParadaRecoleccionResultado desde(
      ParadaRecoleccion parada, ReservaCatalogo reserva) {
    Objects.requireNonNull(parada, "La parada a convertir es obligatoria.");
    return new ParadaRecoleccionResultado(
        parada.id().valor(),
        parada.publicacionId().valor(),
        reserva == null ? null : reserva.tipoResiduo(),
        reserva == null ? null : reserva.pesoKg(),
        parada.estado().name(),
        parada.orden(),
        parada.horarioEstimado().fechaHora(),
        parada.horarioReal() == null ? null : parada.horarioReal().fechaHora(),
        parada.ubicacion().latitud(),
        parada.ubicacion().longitud());
  }
}
