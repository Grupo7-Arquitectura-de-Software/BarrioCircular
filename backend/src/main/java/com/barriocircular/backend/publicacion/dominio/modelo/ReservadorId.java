package com.barriocircular.backend.publicacion.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record ReservadorId(UUID valor) {

    public ReservadorId {
        Objects.requireNonNull(valor, "El ReservadorId no puede ser nulo");
    }

    public static ReservadorId de(UUID valor) {
        return new ReservadorId(valor);
    }
}
