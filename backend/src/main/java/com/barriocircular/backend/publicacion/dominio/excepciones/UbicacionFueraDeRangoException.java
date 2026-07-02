package com.barriocircular.backend.publicacion.dominio.excepciones;

public class UbicacionFueraDeRangoException extends RuntimeException {

  public UbicacionFueraDeRangoException(String mensaje) {
    super(mensaje);
  }
}
