package com.barriocircular.backend.perfiles.aplicacion.dto;

import java.util.UUID;

public record PerfilCapacidadResult(
        UUID perfilId,
        UUID cuentaUsuarioId,
        String rolUsuario,
        String estadoPerfil,
        boolean puedePublicarMateriales,
        boolean puedeComprarMateriales) {
}
