package com.barriocircular.backend.perfiles.aplicacion.dto;

import java.util.UUID;

public record PerfilResultado(
        UUID perfilId,
        UUID cuentaUsuarioId,
        String documentoIdentificacion,
        String nombreCompleto,
        String nombreComercial,
        String rol,
        String estadoPerfil,
        String correoElectronico,
        String telefono,
        double latitud,
        double longitud) {
}
