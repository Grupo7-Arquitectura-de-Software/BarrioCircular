package com.barriocircular.backend.logistica.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record RecicladorId(UUID valor) {

  public RecicladorId {
    Objects.requireNonNull(valor, "El id del reciclador es obligatorio.");
  }

  public static RecicladorId de(UUID valor) {
    return new RecicladorId(valor);
  }
}
