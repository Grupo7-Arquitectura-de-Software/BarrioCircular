package com.barriocircular.backend.perfiles.aplicacion.excepciones;

public class PerfilYaExisteException extends RuntimeException {

  public PerfilYaExisteException(String mensaje) {
    super(mensaje);
  }
}
