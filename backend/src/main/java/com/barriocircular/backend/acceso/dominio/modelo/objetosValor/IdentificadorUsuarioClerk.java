package com.barriocircular.backend.acceso.dominio.modelo.objetosValor;

import com.barriocircular.backend.acceso.dominio.modelo.excepciones.IdentificadorUsuarioClerkExcepcion;

public record IdentificadorUsuarioClerk(String valor) {
    private static final String PREFIJO_CLERK = "user_";

    public IdentificadorUsuarioClerk {
        if (valor == null || valor.isBlank()) {
            throw new IdentificadorUsuarioClerkExcepcion(valor, "El ID de clerk no puede ser vacio o nulo");
        }
        if (!valor.startsWith(PREFIJO_CLERK)) {
            throw new IdentificadorUsuarioClerkExcepcion(valor, "Debe comenzar con el prefijo obligatorio" + PREFIJO_CLERK + ".");
        }
    }
}