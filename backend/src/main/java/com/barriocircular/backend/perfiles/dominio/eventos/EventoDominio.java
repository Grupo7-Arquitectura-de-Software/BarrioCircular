package com.barriocircular.backend.perfiles.dominio.eventos;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventoDominio {

    UUID eventoId();

    LocalDateTime ocurridoEn();
}
