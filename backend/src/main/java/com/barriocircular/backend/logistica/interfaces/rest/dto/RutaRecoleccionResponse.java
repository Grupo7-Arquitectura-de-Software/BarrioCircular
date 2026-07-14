package com.barriocircular.backend.logistica.interfaces.rest.dto;

import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RutaRecoleccionResponse(
    UUID rutaId,
    String estado,
    LocalDate fecha,
    CoordenadaRutaResponse origen,
    List<ParadaRecoleccionResponse> paradas) {

  public RutaRecoleccionResponse {
    paradas = List.copyOf(paradas);
  }

  public static RutaRecoleccionResponse desde(RutaRecoleccionResultado resultado) {
    return new RutaRecoleccionResponse(
        resultado.rutaId(),
        resultado.estado(),
        resultado.fecha(),
        resultado.origen() == null ? null : CoordenadaRutaResponse.desde(resultado.origen()),
        resultado.paradas().stream().map(ParadaRecoleccionResponse::desde).toList());
  }
}
