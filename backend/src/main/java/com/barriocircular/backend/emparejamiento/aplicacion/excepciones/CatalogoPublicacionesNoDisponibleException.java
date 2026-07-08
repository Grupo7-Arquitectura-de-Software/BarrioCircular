package com.barriocircular.backend.emparejamiento.aplicacion.excepciones;

public class CatalogoPublicacionesNoDisponibleException extends RuntimeException {

  public CatalogoPublicacionesNoDisponibleException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
