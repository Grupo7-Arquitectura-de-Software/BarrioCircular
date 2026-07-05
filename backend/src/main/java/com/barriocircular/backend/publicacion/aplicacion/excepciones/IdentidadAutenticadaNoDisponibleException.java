package com.barriocircular.backend.publicacion.aplicacion.excepciones;

public class IdentidadAutenticadaNoDisponibleException extends RuntimeException {

  public IdentidadAutenticadaNoDisponibleException() {
    super("No fue posible obtener la identidad Clerk del usuario autenticado.");
  }
}
