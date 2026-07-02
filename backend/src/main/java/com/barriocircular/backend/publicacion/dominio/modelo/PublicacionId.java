package com.barriocircular.backend.publicacion.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record PublicacionId(UUID valor) {

  public PublicacionId {
    Objects.requireNonNull(valor, "El PublicacionId no puede ser nulo");
  }

  public static PublicacionId nuevo() {
    return new PublicacionId(UUID.randomUUID());
  }

  public static PublicacionId de(UUID valor) {
    return new PublicacionId(valor);
  }
}
