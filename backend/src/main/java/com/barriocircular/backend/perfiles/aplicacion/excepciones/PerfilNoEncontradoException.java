package com.barriocircular.backend.perfiles.aplicacion.excepciones;

public class PerfilNoEncontradoException extends RuntimeException {

    public PerfilNoEncontradoException() {
        super("No existe un perfil de usuario con el identificador proporcionado.");
    }
}
