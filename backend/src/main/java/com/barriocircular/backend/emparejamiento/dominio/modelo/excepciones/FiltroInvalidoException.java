package com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones;

public class FiltroInvalidoException extends EmparejamientoDominioException {
  public FiltroInvalidoException(String mensaje) {
    super(mensaje);
  }

  public FiltroInvalidoException(String mensaje, double valor) {
    super(String.format("%s %s", mensaje, valor));
  }
}
