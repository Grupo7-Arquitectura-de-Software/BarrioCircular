package com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones;

public class CuentaNoActivaException extends RuntimeException {

  public CuentaNoActivaException() {
    super("La cuenta no esta activa.");
  }
}
