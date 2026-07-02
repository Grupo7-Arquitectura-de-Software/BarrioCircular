package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;

public record EvidenciaVisual(String url) {

  public EvidenciaVisual {
    if (url == null || url.isBlank()) {
      throw new PublicacionInvalidaException(
          "La evidencia visual (URL) es obligatoria al crear la publicación.");
    }
    if (!url.startsWith("https://")) {
      throw new PublicacionInvalidaException(
          "La URL de evidencia debe ser HTTPS. Recibido: " + url);
    }
  }
}
