package com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones;

public class PerfilNoActivoException extends RuntimeException {

  public PerfilNoActivoException() {
    super("El perfil no esta activo.");
  }
}
