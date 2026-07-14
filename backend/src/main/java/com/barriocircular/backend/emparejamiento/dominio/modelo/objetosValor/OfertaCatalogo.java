package com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor;

import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.OfertaCatalogoInvalidaException;
import java.math.BigDecimal;
import java.util.UUID;

public record OfertaCatalogo(
    UUID publicacionId,
    TipoMaterialFiltro tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String estado,
    String creadorRol) {
  public OfertaCatalogo {
    if (publicacionId == null) {
      throw new OfertaCatalogoInvalidaException("La oferta de catalogo requiere un publicacionId.");
    }
    if (tipoResiduo == null) {
      throw new OfertaCatalogoInvalidaException("La oferta de catalogo requiere un tipoResiduo.");
    }
    if (!Double.isFinite(pesoKg) || pesoKg <= 0) {
      throw new OfertaCatalogoInvalidaException(
          "El pesoKg de la oferta de catalogo debe ser mayor que 0. Recibido: " + pesoKg);
    }
    if (precioPorKilo == null || precioPorKilo.signum() <= 0) {
      throw new OfertaCatalogoInvalidaException(
          "El precioPorKilo de la oferta de catalogo debe ser mayor que 0. Recibido: "
              + precioPorKilo);
    }
    if (estado == null || estado.isBlank()) {
      throw new OfertaCatalogoInvalidaException(
          "La oferta de catalogo requiere un estado no vacio.");
    }

    new CoordenadaGPS(latitud, longitud);
  }
}
