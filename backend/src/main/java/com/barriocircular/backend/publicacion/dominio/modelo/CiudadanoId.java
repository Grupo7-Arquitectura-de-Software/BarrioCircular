package com.barriocircular.backend.publicacion.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record CiudadanoId(UUID valor) {

    public CiudadanoId {
        Objects.requireNonNull(valor, "El CiudadanoId no puede ser nulo");
    }

    public static CiudadanoId de(UUID valor) {
        return new CiudadanoId(valor);
    }
}
