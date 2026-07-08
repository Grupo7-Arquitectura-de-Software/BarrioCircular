package com.barriocircular.backend.sugerenciaprecio.dominio.excepciones;

public class TipoMaterialSugeridoInvalidoException extends RuntimeException {

  public TipoMaterialSugeridoInvalidoException(String tipoRecibido) {
    super("El tipo de material '" + tipoRecibido + "' no pertenece al catálogo soportado.");
  }
}
