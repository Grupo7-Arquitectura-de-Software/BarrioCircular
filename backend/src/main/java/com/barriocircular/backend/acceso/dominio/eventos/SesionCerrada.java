package com.barriocircular.backend.acceso.dominio.eventos;

import java.time.Instant;
import java.util.UUID;

public record SesionCerrada(UUID cuentaId, Instant ocurridoEn) implements EventoDominio {
}