package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class CorreoDuplicadoException extends AccesoDominioExcepcion {
    public CorreoDuplicadoException(String correoElectronico) {
        super(String.format("Ya existe una cuenta registrada con el correo '%s'.", correoElectronico));
    }
}