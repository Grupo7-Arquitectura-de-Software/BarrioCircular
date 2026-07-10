package com.barriocircular.backend.logistica.dominio.objetosValor;

public record CoordenadaGPS(double latitud, double longitud) {

  private static final double LATITUD_MINIMA = -90;
  private static final double LATITUD_MAXIMA = 90;
  private static final double LONGITUD_MINIMA = -180;
  private static final double LONGITUD_MAXIMA = 180;

  public CoordenadaGPS {
    validarLatitud(latitud);
    validarLongitud(longitud);
  }

  private static void validarLatitud(double latitud) {
    if (!Double.isFinite(latitud) || latitud < LATITUD_MINIMA || latitud > LATITUD_MAXIMA) {
      throw new IllegalArgumentException("La latitud debe estar en el rango [-90, 90].");
    }
  }

  private static void validarLongitud(double longitud) {
    if (!Double.isFinite(longitud) || longitud < LONGITUD_MINIMA || longitud > LONGITUD_MAXIMA) {
      throw new IllegalArgumentException("La longitud debe estar en el rango [-180, 180].");
    }
  }
}
