package com.barriocircular.backend.perfiles.aplicacion.comandos;

import java.util.UUID;

public record CrearPerfilCommand(
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
