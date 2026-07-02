package com.barriocircular.backend.publicacion.aplicacion.comandos;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearPublicacionCommand(
    UUID creadorId,
    String tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String evidenciaUrl) {}
