package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;

public enum EstadoPublicacion {
  DISPONIBLE,
  RESERVADA,
  FINALIZADA,
  CANCELADA;

  public boolean puedeTransicionarA(EstadoPublicacion destino) {
    if (this == DISPONIBLE) {
      return destino == RESERVADA || destino == CANCELADA;
    }
    if (this == RESERVADA) {
      return destino == FINALIZADA || destino == CANCELADA;
    }
    return false;
  }

  public EstadoPublicacion transicionarA(EstadoPublicacion destino) {
    if (!puedeTransicionarA(destino)) {
      throw new EstadoInvalidoException(
          "Transición de estado no permitida: " + this + " → " + destino);
    }
    return destino;
  }

  public boolean esTerminal() {
    return this == FINALIZADA || this == CANCELADA;
  }
}
