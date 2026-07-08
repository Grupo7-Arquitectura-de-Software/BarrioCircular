package com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor;

import java.util.Objects;
import java.util.UUID;

public record CompradorId(UUID valor) {

  public CompradorId {
    Objects.requireNonNull(valor, "El CompradorId no puede ser nulo.");
  }

  public static CompradorId de(UUID valor) {
    return new CompradorId(valor);
  }
}
