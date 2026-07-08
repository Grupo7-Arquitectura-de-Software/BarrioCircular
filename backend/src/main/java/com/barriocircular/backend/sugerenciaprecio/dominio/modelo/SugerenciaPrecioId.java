package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import java.util.Objects;
import java.util.UUID;

public record SugerenciaPrecioId(UUID valor) {

  public SugerenciaPrecioId {
    Objects.requireNonNull(valor, "El SugerenciaPrecioId no puede ser nulo");
  }

  public static SugerenciaPrecioId nuevo() {
    return new SugerenciaPrecioId(UUID.randomUUID());
  }

  public static SugerenciaPrecioId de(UUID valor) {
    return new SugerenciaPrecioId(valor);
  }
}
