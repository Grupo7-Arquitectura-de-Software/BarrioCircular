package com.barriocircular.backend.logistica.interfaces.rest.dto;

import com.barriocircular.backend.logistica.aplicacion.dto.CoordenadaRutaResultado;

public record CoordenadaRutaResponse(double latitud, double longitud) {

  public static CoordenadaRutaResponse desde(CoordenadaRutaResultado resultado) {
    return new CoordenadaRutaResponse(resultado.latitud(), resultado.longitud());
  }
}
