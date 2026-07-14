package com.barriocircular.backend.publicacion.aplicacion.excepciones;

public class PerfilNoEncontradoException extends RuntimeException {

  public PerfilNoEncontradoException() {
    super("No existe un perfil de usuario para la identidad autenticada.");
  }
}
