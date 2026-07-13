package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Estado de conservación del material observado en la foto. Su factor acota cuánto puede variar el
 * precio sugerido respecto al precio base de mercado: la IA solo clasifica el estado, nunca decide
 * el precio.
 */
public enum EstadoMaterial {
  EXCELENTE(new BigDecimal("1.0")),
  BUENO(new BigDecimal("0.9")),
  REGULAR(new BigDecimal("0.8"));

  private final BigDecimal factor;

  EstadoMaterial(BigDecimal factor) {
    this.factor = factor;
  }

  public BigDecimal factor() {
    return factor;
  }

  /**
   * A diferencia de {@link TipoMaterialSugerido#desde}, un estado no reconocido no es un error del
   * cliente sino una respuesta imprecisa de la IA: se devuelve vacío para que el caso de uso
   * aplique un valor por defecto.
   */
  public static Optional<EstadoMaterial> desde(String valor) {
    try {
      return Optional.of(EstadoMaterial.valueOf(valor));
    } catch (IllegalArgumentException | NullPointerException excepcion) {
      return Optional.empty();
    }
  }
}
