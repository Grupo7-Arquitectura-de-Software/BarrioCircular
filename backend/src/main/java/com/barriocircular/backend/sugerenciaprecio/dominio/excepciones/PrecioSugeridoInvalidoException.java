package com.barriocircular.backend.sugerenciaprecio.dominio.excepciones;

public class PrecioSugeridoInvalidoException extends RuntimeException {

  public PrecioSugeridoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
