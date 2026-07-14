package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record AnalisisMaterialId(UUID valor) {

  public AnalisisMaterialId {
    Objects.requireNonNull(valor, "El AnalisisMaterialId no puede ser nulo");
  }

  public static AnalisisMaterialId nuevo() {
    return new AnalisisMaterialId(UUID.randomUUID());
  }

  public static AnalisisMaterialId de(UUID valor) {
    return new AnalisisMaterialId(valor);
  }
}
