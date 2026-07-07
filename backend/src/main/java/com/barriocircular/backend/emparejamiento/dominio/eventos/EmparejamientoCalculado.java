package com.barriocircular.backend.emparejamiento.dominio.eventos;

import java.time.Instant;
import java.util.UUID;

public record EmparejamientoCalculado(
        UUID resultadoId, UUID compradorId, int cantidadOfertasEncontradas, Instant ocurridoEn)
        implements EventoDominio {
}