package com.barriocircular.backend.perfiles.aplicacion.excepciones;

public class PerfilYaExisteException extends IllegalStateException {

  public PerfilYaExisteException(String mensaje) {
    super(mensaje);
  }
}
