package com.barriocircular.backend.perfiles.dominio.excepciones;

public class PerfilSuspendidoException extends PerfilDomainException {

  public PerfilSuspendidoException(String mensaje) {
    super(mensaje);
  }
}
