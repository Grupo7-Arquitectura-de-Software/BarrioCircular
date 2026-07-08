package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;

/**
 * Catálogo de materiales soportado. Definido localmente (ACL) en vez de reutilizar el TipoResiduo
 * de Publicación: si ese catálogo cambia, este enum se actualiza a mano.
 */
public enum TipoMaterialSugerido {
  PET,
  CARTON,
  VIDRIO,
  CHATARRA;

  public static TipoMaterialSugerido desde(String valor) {
    try {
      return TipoMaterialSugerido.valueOf(valor);
    } catch (IllegalArgumentException | NullPointerException excepcion) {
      throw new TipoMaterialSugeridoInvalidoException(String.valueOf(valor));
    }
  }
}
