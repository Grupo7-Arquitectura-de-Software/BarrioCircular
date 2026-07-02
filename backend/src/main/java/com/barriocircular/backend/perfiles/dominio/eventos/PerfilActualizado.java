package com.barriocircular.backend.perfiles.dominio.eventos;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record PerfilActualizado(
    UUID eventoId, UUID perfilId, String descripcionCambio, LocalDateTime ocurridoEn)
    implements EventoDominio {

  public PerfilActualizado(UUID perfilId, String descripcionCambio, LocalDateTime ocurridoEn) {
    this(UUID.randomUUID(), perfilId, descripcionCambio, ocurridoEn);
  }

  public PerfilActualizado {
    Objects.requireNonNull(eventoId, "El identificador del evento es obligatorio");
    Objects.requireNonNull(perfilId, "El identificador del perfil es obligatorio");
    if (descripcionCambio == null || descripcionCambio.isBlank()) {
      throw new IllegalArgumentException("La descripcion del cambio es obligatoria");
    }
    Objects.requireNonNull(ocurridoEn, "La fecha del evento es obligatoria");
  }
}
