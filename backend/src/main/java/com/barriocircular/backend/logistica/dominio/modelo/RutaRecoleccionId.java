package com.barriocircular.backend.logistica.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record RutaRecoleccionId(UUID valor) {

  public RutaRecoleccionId {
    Objects.requireNonNull(valor, "El id de la ruta de recoleccion es obligatorio.");
  }

  public static RutaRecoleccionId nuevo() {
    return new RutaRecoleccionId(UUID.randomUUID());
  }

  public static RutaRecoleccionId de(UUID valor) {
    return new RutaRecoleccionId(valor);
  }
}
