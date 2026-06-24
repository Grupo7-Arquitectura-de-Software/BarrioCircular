package com.barriocircular.backend.acceso.dominio.eventos;

import java.time.Instant;
import java.util.UUID;

public record SesionIniciada(UUID cuentaId, Instant ocurridoEn) implements EventoDominio {
}