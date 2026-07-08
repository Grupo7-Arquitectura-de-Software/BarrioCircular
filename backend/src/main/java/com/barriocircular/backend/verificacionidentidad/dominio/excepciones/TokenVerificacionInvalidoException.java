package com.barriocircular.backend.verificacionidentidad.dominio.excepciones;

public class TokenVerificacionInvalidoException extends VerificacionIdentidadException {

  public TokenVerificacionInvalidoException() {
    super("El token de verificacion no es valido.");
  }
}
