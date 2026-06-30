package com.barriocircular.backend.perfiles.aplicacion.excepciones;

public class CuentaAccesoNoEncontradaException extends RuntimeException {

    public CuentaAccesoNoEncontradaException() {
        super("No existe una cuenta de acceso asociada al usuario autenticado.");
    }
}
