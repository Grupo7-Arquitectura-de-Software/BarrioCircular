package com.barriocircular.backend.emparejamiento.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OfertaRecomendadaResponse(
    UUID publicacionId, double distanciaKm, BigDecimal precioPorKilo, double scoreTotal) {}
