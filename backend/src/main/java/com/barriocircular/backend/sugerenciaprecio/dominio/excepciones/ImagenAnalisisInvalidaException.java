package com.barriocircular.backend.sugerenciaprecio.dominio.excepciones;

public class ImagenAnalisisInvalidaException extends RuntimeException {

  public ImagenAnalisisInvalidaException() {
    super("La imagen a analizar es obligatoria y debe ser un data URI de imagen (data:image/...).");
  }
}
