package com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor;

import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.PuntajeInvalidoException;

import java.math.BigDecimal;
import java.util.UUID;

public record PuntajeOferta(UUID publicacionId, double distanciaKm, BigDecimal precioKg, double scoreTotal) {

    public static final double DISTANCIA_MINIMA_KM = 0.001;

    public PuntajeOferta {
        if (publicacionId == null) {
            throw new PuntajeInvalidoException("El publicacionId no puede ser nulo.");
        }
        if (precioKg == null) {
            throw new PuntajeInvalidoException("El precioKg no puede ser nulo.");
        }
        if (!Double.isFinite(distanciaKm) || distanciaKm < DISTANCIA_MINIMA_KM) {
            throw new PuntajeInvalidoException(
                    "La distancia debe ser mayor que 0 para calcular el scoreTotal. Minimo permitido: 0.001 km. Recibido: "
                            + distanciaKm
            );
        }
        if (!Double.isFinite(scoreTotal)) {
            throw new PuntajeInvalidoException("El scoreTotal debe ser un valor numerico finito.");
        }
    }
}