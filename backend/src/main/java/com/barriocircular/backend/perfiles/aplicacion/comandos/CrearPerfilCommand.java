package com.barriocircular.backend.perfiles.aplicacion.comandos;

public record CrearPerfilCommand(
    String documentoIdentificacion,
    String nombreCompleto,
    String nombreComercial,
    String rol,
    String correoElectronico,
    String telefono,
    double latitud,
    double longitud) {}
