package com.barriocircular.backend.publicacion.aplicacion.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PublicacionResultado(
    UUID publicacionId,
    UUID creadorId,
    String tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String evidenciaUrl,
    String estado,
    Instant fechaCreacion,
    UUID reservadoPor) {}
