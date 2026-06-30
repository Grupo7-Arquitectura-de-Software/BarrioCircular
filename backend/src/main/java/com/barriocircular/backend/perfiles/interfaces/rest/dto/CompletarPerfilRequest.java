package com.barriocircular.backend.perfiles.interfaces.rest.dto;

import java.util.UUID;

public record CompletarPerfilRequest(
        UUID cuentaUsuarioId,
        String documentoIdentificacion,
        String nombreCompleto,
        String nombreComercial,
        String rol,
        String correoElectronico,
        String telefono,
        double latitud,
        double longitud) {
}
