package com.barriocircular.backend.publicacion.aplicacion.excepciones;

public class PerfilNoAutorizadoException extends RuntimeException {

  public PerfilNoAutorizadoException(String mensaje) {
    super(mensaje);
  }
}
