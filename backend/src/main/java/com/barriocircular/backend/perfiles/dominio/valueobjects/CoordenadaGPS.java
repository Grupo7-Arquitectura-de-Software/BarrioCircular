package com.barriocircular.backend.perfiles.dominio.valueobjects;

import com.barriocircular.backend.perfiles.dominio.excepciones.UbicacionFueraDeQuitoException;

public record CoordenadaGPS(double latitud, double longitud) {

  private static final double LATITUD_MINIMA_QUITO = -0.50;
  private static final double LATITUD_MAXIMA_QUITO = 0.10;
  private static final double LONGITUD_MINIMA_QUITO = -78.70;
  private static final double LONGITUD_MAXIMA_QUITO = -78.20;

  public CoordenadaGPS {
    if (!Double.isFinite(latitud)
        || !Double.isFinite(longitud)
        || latitud < -90
        || latitud > 90
        || longitud < -180
        || longitud > 180) {
      throw new UbicacionFueraDeQuitoException(
          "La coordenada GPS no se encuentra dentro de los rangos globales validos");
    }
    if (latitud < LATITUD_MINIMA_QUITO
        || latitud > LATITUD_MAXIMA_QUITO
        || longitud < LONGITUD_MINIMA_QUITO
        || longitud > LONGITUD_MAXIMA_QUITO) {
      throw new UbicacionFueraDeQuitoException(
          "La ubicacion habitual del perfil debe estar dentro del rango operativo de Quito");
    }
  }

  public double getLatitud() {
    return latitud;
  }

  public double getLongitud() {
    return longitud;
  }
}
