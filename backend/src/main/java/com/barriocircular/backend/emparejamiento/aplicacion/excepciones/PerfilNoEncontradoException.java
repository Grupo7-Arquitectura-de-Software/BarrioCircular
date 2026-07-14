package com.barriocircular.backend.emparejamiento.aplicacion.excepciones;

public class PerfilNoEncontradoException extends RuntimeException {

  public PerfilNoEncontradoException() {
    super("No existe un perfil de negocio asociado a la identidad autenticada.");
  }
}
