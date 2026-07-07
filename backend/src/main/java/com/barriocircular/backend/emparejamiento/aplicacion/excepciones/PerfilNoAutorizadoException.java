package com.barriocircular.backend.emparejamiento.aplicacion.excepciones;

public class PerfilNoAutorizadoException extends RuntimeException {

    public PerfilNoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
