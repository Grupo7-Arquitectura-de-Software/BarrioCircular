package com.barriocircular.backend.perfiles.interfaces.rest.dto;

public record CompletarPerfilRequest(
    String documentoIdentificacion,
    String nombreCompleto,
    String nombreComercial,
    String rol,
    String correoElectronico,
    String telefono,
    double latitud,
    double longitud) {}
