package com.barriocircular.backend.logistica.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record PublicacionId(UUID valor) {

  public PublicacionId {
    Objects.requireNonNull(valor, "El id de la publicacion reservada es obligatorio.");
  }

  public static PublicacionId de(UUID valor) {
    return new PublicacionId(valor);
  }
}
