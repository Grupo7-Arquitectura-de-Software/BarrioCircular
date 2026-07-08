package com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones;

public abstract class EmparejamientoDominioException extends RuntimeException {
  protected EmparejamientoDominioException(String mensaje) {
    super(mensaje);
  }
}
