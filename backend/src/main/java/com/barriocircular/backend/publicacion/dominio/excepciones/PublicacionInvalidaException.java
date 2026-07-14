package com.barriocircular.backend.publicacion.dominio.excepciones;

public class PublicacionInvalidaException extends RuntimeException {

  public PublicacionInvalidaException(String mensaje) {
    super(mensaje);
  }

  public PublicacionInvalidaException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
