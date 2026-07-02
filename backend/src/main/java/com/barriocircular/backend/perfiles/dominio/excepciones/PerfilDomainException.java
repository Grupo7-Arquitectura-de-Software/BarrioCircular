package com.barriocircular.backend.perfiles.dominio.excepciones;

public class PerfilDomainException extends RuntimeException {

  public PerfilDomainException(String mensaje) {
    super(mensaje);
  }
}
