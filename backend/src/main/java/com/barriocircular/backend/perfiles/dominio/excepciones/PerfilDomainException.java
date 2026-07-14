package com.barriocircular.backend.perfiles.dominio.excepciones;

public class PerfilDomainException extends IllegalArgumentException {

  public PerfilDomainException(String mensaje) {
    super(mensaje);
  }
}
