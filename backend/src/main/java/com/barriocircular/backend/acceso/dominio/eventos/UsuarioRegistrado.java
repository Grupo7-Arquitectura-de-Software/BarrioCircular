package com.barriocircular.backend.acceso.dominio.eventos;

import java.time.Instant;
import java.util.UUID;

public record UsuarioRegistrado(
        UUID cuentaId,
        String clerkId,
        String correoElectronico,
        Instant ocurridoEn
) {
}