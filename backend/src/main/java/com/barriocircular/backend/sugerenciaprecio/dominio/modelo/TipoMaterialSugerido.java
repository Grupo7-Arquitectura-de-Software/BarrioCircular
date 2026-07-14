package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;

/**
 * Catálogo de materiales soportado. Definido localmente (ACL) en vez de reutilizar el TipoResiduo
 * de Publicación: si ese catálogo cambia, este enum se actualiza a mano.
 *
 * <p>Cada material lleva el peso máximo razonable que puede aparecer en una sola foto: el PET es
 * liviano (una foto difícilmente muestra más de 100 kg de botellas), mientras la chatarra puede
 * pesar mucho más. Un peso estimado por encima del tope se trata como alucinación de la IA.
 */
public enum TipoMaterialSugerido {
  PET(100.0),
  CARTON(200.0),
  VIDRIO(300.0),
  CHATARRA(500.0);

  private final double pesoMaximoRazonableKg;

  TipoMaterialSugerido(double pesoMaximoRazonableKg) {
    this.pesoMaximoRazonableKg = pesoMaximoRazonableKg;
  }

  public double pesoMaximoRazonableKg() {
    return pesoMaximoRazonableKg;
  }

  public static TipoMaterialSugerido desde(String valor) {
    try {
      return TipoMaterialSugerido.valueOf(valor);
    } catch (IllegalArgumentException | NullPointerException excepcion) {
      throw new TipoMaterialSugeridoInvalidoException(String.valueOf(valor));
    }
  }
}
