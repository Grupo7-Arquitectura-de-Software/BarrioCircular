package com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones;

public class CoordenadaInvalidaException extends EmparejamientoDominioException {
    public CoordenadaInvalidaException(String mensaje, double valor) {
        super(String.format("%s %s", mensaje, valor));
    }
}
