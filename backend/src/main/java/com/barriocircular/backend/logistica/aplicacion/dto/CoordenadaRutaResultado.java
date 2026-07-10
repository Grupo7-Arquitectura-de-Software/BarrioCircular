package com.barriocircular.backend.logistica.aplicacion.dto;

import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;

public record CoordenadaRutaResultado(double latitud, double longitud) {

  public static CoordenadaRutaResultado desde(CoordenadaGPS coordenada) {
    return new CoordenadaRutaResultado(coordenada.latitud(), coordenada.longitud());
  }
}
