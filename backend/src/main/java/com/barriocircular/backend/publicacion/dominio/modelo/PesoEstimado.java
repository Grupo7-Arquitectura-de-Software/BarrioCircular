package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;

public record PesoEstimado(double valorKg) {

  public PesoEstimado {
    if (valorKg <= 0) {
      throw new PublicacionInvalidaException(
          "El peso estimado debe ser mayor que 0 kg. Recibido: " + valorKg);
    }
  }

  public static PesoEstimado deKilos(double valorKg) {
    return new PesoEstimado(valorKg);
  }
}
