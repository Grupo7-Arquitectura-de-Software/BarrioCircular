package com.barriocircular.backend.verificacionidentidad.dominio.excepciones;

public class CredencialYaRevocadaException extends VerificacionIdentidadException {

  public CredencialYaRevocadaException() {
    super("Una credencial revocada no puede volver a activarse.");
  }
}
