package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.PrecioSugeridoInvalidoException;
import java.math.BigDecimal;

/**
 * Techo de cordura contra alucinaciones del modelo de IA: un precio fuera de este rango se descarta
 * y se reemplaza por el catálogo de respaldo.
 */
public record PrecioSugerido(BigDecimal valor) {

  private static final BigDecimal LIMITE_MAXIMO = new BigDecimal("10.00");

  public PrecioSugerido {
    if (valor == null || valor.signum() <= 0) {
      throw new PrecioSugeridoInvalidoException(
          "El precio sugerido debe ser mayor que 0. Recibido: " + valor);
    }
    if (valor.compareTo(LIMITE_MAXIMO) > 0) {
      throw new PrecioSugeridoInvalidoException(
          "El precio sugerido no puede superar " + LIMITE_MAXIMO + " USD/kg. Recibido: " + valor);
    }
  }

  public static PrecioSugerido de(double valor) {
    return new PrecioSugerido(BigDecimal.valueOf(valor));
  }
}
