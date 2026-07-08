package com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones;

public class IdentidadAutenticadaNoDisponibleException extends RuntimeException {

  public IdentidadAutenticadaNoDisponibleException() {
    super("No fue posible obtener la identidad autenticada.");
  }
}
