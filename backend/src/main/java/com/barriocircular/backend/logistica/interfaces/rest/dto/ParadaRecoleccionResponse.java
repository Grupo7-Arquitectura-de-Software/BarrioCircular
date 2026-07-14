package com.barriocircular.backend.logistica.interfaces.rest.dto;

import com.barriocircular.backend.logistica.aplicacion.dto.ParadaRecoleccionResultado;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ParadaRecoleccionResponse(
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

  public static ParadaRecoleccionResponse desde(ParadaRecoleccionResultado resultado) {
    return new ParadaRecoleccionResponse(
        resultado.paradaId(),
        resultado.publicacionId(),
        resultado.tipoResiduo(),
        resultado.pesoKg(),
        resultado.estado(),
        resultado.orden(),
        resultado.horaLlegadaEstimada(),
        resultado.horaLlegadaReal(),
        resultado.latitud(),
        resultado.longitud());
  }
}
