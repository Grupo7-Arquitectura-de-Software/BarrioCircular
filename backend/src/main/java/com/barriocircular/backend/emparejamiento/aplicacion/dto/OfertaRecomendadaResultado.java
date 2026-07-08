package com.barriocircular.backend.emparejamiento.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OfertaRecomendadaResultado(
    UUID publicacionId, double distanciaKm, BigDecimal precioPorKilo, double scoreTotal) {}
