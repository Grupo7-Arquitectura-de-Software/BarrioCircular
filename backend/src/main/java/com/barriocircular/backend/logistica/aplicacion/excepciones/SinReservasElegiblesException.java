package com.barriocircular.backend.logistica.aplicacion.excepciones;

public class SinReservasElegiblesException extends RuntimeException {

  public SinReservasElegiblesException(String message) {
    super(message);
  }
}
