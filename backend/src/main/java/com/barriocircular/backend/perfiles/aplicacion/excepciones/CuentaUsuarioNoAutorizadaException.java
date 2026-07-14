package com.barriocircular.backend.perfiles.aplicacion.excepciones;

public class CuentaUsuarioNoAutorizadaException extends RuntimeException {

  public CuentaUsuarioNoAutorizadaException() {
    super("La cuenta indicada no pertenece al usuario autenticado.");
  }
}
