package com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones;

public class PerfilNoEncontradoException extends RuntimeException {

  public PerfilNoEncontradoException() {
    super("No se encontro el perfil asociado a la identidad autenticada.");
  }
}
