package com.barriocircular.backend.logistica.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record ParadaRecoleccionId(UUID valor) {

  public ParadaRecoleccionId {
    Objects.requireNonNull(valor, "El id de la parada de recoleccion es obligatorio.");
  }

  public static ParadaRecoleccionId nuevo() {
    return new ParadaRecoleccionId(UUID.randomUUID());
  }

  public static ParadaRecoleccionId de(UUID valor) {
    return new ParadaRecoleccionId(valor);
  }
}
