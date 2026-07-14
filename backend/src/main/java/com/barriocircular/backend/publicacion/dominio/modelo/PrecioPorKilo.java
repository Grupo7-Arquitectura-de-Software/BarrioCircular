package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;
import java.math.BigDecimal;

public record PrecioPorKilo(BigDecimal valor) {

  public PrecioPorKilo {
    if (valor == null || valor.signum() <= 0) {
      throw new PublicacionInvalidaException(
          "El precio por kilo debe ser mayor que 0. Recibido: " + valor);
    }
  }

  public static PrecioPorKilo de(double valor) {
    return new PrecioPorKilo(BigDecimal.valueOf(valor));
  }
}
