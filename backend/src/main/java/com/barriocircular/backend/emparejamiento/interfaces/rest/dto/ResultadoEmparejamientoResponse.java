package com.barriocircular.backend.emparejamiento.interfaces.rest.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResultadoEmparejamientoResponse(
    UUID resultadoId, Instant fechaCalculo, List<OfertaRecomendadaResponse> ofertas) {}
