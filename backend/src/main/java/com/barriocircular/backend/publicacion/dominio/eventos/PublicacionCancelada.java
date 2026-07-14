package com.barriocircular.backend.publicacion.dominio.eventos;

import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import java.time.Instant;

public record PublicacionCancelada(PublicacionId publicacionId, Instant ocurridoEn)
    implements EventoDominio {}
