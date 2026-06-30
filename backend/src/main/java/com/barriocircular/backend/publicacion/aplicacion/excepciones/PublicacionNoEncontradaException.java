package com.barriocircular.backend.publicacion.aplicacion.excepciones;

public class PublicacionNoEncontradaException extends RuntimeException {

    public PublicacionNoEncontradaException() {
        super("No existe una publicacion con el identificador proporcionado.");
    }
}
