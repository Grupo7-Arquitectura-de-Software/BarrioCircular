package com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones;

public class PosicionFueraDeRangoException extends EmparejamientoDominioException {
  public PosicionFueraDeRangoException(String mensaje) {
    super(mensaje);
  }
}
