package com.barriocircular.backend.verificacionidentidad.dominio.modelo;

import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.TokenVerificacionInvalidoException;

public record TokenVerificacion(String valor) {

  private static final int LONGITUD_MINIMA = 32;

  public TokenVerificacion {
    if (valor == null || valor.isBlank() || valor.trim().length() < LONGITUD_MINIMA) {
      throw new TokenVerificacionInvalidoException();
    }
    valor = valor.trim();
  }
}
