package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class EstadoTransicionInvalidaException extends AccesoDominioExcepcion {
    public EstadoTransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}