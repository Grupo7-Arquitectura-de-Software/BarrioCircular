package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.UbicacionFueraDeRangoException;

public record UbicacionRecogida(double latitud, double longitud) {

    private static final double LAT_MIN = -0.50;
    private static final double LAT_MAX = 0.10;
    private static final double LON_MIN = -78.70;
    private static final double LON_MAX = -78.20;

    public UbicacionRecogida {
        if (latitud < LAT_MIN || latitud > LAT_MAX
                || longitud < LON_MIN || longitud > LON_MAX) {
            throw new UbicacionFueraDeRangoException(
                    "La ubicación de recogida debe estar dentro de Quito. Recibido: ("
                            + latitud + ", " + longitud + ")");
        }
    }
}
