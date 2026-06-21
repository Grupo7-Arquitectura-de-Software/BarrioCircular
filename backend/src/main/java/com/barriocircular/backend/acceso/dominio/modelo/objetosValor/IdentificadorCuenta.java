package com.barriocircular.backend.acceso.dominio.modelo.objetosValor;

import java.util.UUID;

public record IdentificadorCuenta(UUID uuid) {
    public IdentificadorCuenta {
        if (uuid == null) {
            throw new IllegalArgumentException("El uuid no puede ser nulo");
        }
    }

    public static IdentificadorCuenta nuevo() {
        return new IdentificadorCuenta(UUID.randomUUID());
    }
}
