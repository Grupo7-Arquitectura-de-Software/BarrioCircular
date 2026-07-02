package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;

public record DetalleMaterial(TipoResiduo tipo, PesoEstimado peso) {

  public DetalleMaterial {
    if (tipo == null) {
      throw new PublicacionInvalidaException(
          "El tipo de residuo es obligatorio y debe pertenecer al catálogo oficial.");
    }
    if (peso == null) {
      throw new PublicacionInvalidaException("El peso estimado es obligatorio.");
    }
  }
}
