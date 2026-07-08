package com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor;

import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.CoordenadaInvalidaException;

public record CoordenadaGPS(double latitud, double longitud) {
    public static final double LIMITE_INFERIOR_LATITUD = -90;
    public static final double LIMITE_SUPERIOR_LATITUD = 90;
    public static final double LIMITE_SUPERIOR_LONGITUD = 180;
    public static final double LIMITE_INFERIOR_LONGITUD = -180;

    public CoordenadaGPS {
        validarLatitud(latitud);
        validarLongitud(longitud);
    }

    private static void validarLatitud(double latitud) {
        if (!Double.isFinite(latitud)
                || latitud < LIMITE_INFERIOR_LATITUD
                || latitud > LIMITE_SUPERIOR_LATITUD) {
            throw new CoordenadaInvalidaException("Latitud fuera de rango [-90, 90]:", latitud);
        }
    }

    private static void validarLongitud(double longitud) {
        if (!Double.isFinite(longitud)
                || longitud < LIMITE_INFERIOR_LONGITUD
                || longitud > LIMITE_SUPERIOR_LONGITUD) {
            throw new CoordenadaInvalidaException("Longitud fuera de rango [-180, 180]:", longitud);
        }
    }
}