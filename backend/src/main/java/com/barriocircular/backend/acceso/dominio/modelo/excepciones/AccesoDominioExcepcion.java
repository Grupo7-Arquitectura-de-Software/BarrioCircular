package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public abstract class AccesoDominioExcepcion extends RuntimeException {
  protected AccesoDominioExcepcion(String mensaje) {
    super(mensaje);
  }

  protected AccesoDominioExcepcion(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
