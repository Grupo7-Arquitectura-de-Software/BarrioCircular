package com.barriocircular.backend.verificacionidentidad.dominio.excepciones;

public class RolNoElegibleException extends VerificacionIdentidadException {

  public RolNoElegibleException(String rol) {
    super("El rol " + rol + " no es elegible para emitir credenciales de identidad.");
  }
}
