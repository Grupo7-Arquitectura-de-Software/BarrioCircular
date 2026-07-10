package com.barriocircular.backend.logistica.dominio.servicios;

import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import java.util.Objects;

public final class CalculadorDistanciaGeografica {

  private static final double RADIO_TIERRA_KM = 6371.0;

  public double distanciaKm(CoordenadaGPS origen, CoordenadaGPS destino) {
    Objects.requireNonNull(origen, "El origen es obligatorio.");
    Objects.requireNonNull(destino, "El destino es obligatorio.");

    double deltaLatitudRad = Math.toRadians(destino.latitud() - origen.latitud());
    double deltaLongitudRad = Math.toRadians(destino.longitud() - origen.longitud());
    double latitudOrigenRad = Math.toRadians(origen.latitud());
    double latitudDestinoRad = Math.toRadians(destino.latitud());

    double a =
        Math.sin(deltaLatitudRad / 2) * Math.sin(deltaLatitudRad / 2)
            + Math.cos(latitudOrigenRad)
                * Math.cos(latitudDestinoRad)
                * Math.sin(deltaLongitudRad / 2)
                * Math.sin(deltaLongitudRad / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return RADIO_TIERRA_KM * c;
  }
}
