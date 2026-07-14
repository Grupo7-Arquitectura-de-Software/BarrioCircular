package com.barriocircular.backend.publicacion.aplicacion.comandos;

import java.math.BigDecimal;

public record CrearPublicacionCommand(
    String tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String evidenciaUrl) {}
