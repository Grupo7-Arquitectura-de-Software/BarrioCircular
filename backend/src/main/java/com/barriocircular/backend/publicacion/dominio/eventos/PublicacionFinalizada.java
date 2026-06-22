package com.barriocircular.backend.publicacion.dominio.eventos;

import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;

import java.time.Instant;

public record PublicacionFinalizada(
        PublicacionId publicacionId,
        ReservadorId reservador,
        Instant ocurridoEn
) implements EventoDominio {
}
