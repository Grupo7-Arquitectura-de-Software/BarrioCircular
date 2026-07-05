package com.barriocircular.backend.publicacion.interfaces.rest.dto;

import java.math.BigDecimal;

public record CrearPublicacionRequest(
    String tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String evidenciaUrl) {}
