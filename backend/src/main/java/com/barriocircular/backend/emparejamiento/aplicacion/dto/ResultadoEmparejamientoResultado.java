package com.barriocircular.backend.emparejamiento.aplicacion.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResultadoEmparejamientoResultado(
    UUID resultadoId,
    UUID compradorId,
    double latitudOrigen,
    double longitudOrigen,
    double radioMaximoKm,
    Instant fechaCalculo,
    List<OfertaRecomendadaResultado> ofertas) {}
