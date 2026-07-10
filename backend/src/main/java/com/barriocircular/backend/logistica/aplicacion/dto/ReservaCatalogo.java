package com.barriocircular.backend.logistica.aplicacion.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ReservaCatalogo(
    UUID publicacionId,
    UUID vendedorId,
    String tipoResiduo,
    double pesoKg,
    double latitud,
    double longitud,
    Instant fechaReserva) {

  public ReservaCatalogo {
    Objects.requireNonNull(publicacionId, "El id de la publicacion es obligatorio.");
    Objects.requireNonNull(vendedorId, "El id del vendedor es obligatorio.");
    if (tipoResiduo == null || tipoResiduo.isBlank()) {
      throw new IllegalArgumentException("El tipo de residuo es obligatorio.");
    }
    if (!Double.isFinite(pesoKg) || pesoKg <= 0) {
      throw new IllegalArgumentException("El peso de la reserva debe ser mayor que cero.");
    }
    validarCoordenada(latitud, longitud);
    Objects.requireNonNull(fechaReserva, "La fecha de reserva es obligatoria.");
  }

  private static void validarCoordenada(double latitud, double longitud) {
    if (!Double.isFinite(latitud) || latitud < -90 || latitud > 90) {
      throw new IllegalArgumentException("La latitud de la reserva esta fuera de rango.");
    }
    if (!Double.isFinite(longitud) || longitud < -180 || longitud > 180) {
      throw new IllegalArgumentException("La longitud de la reserva esta fuera de rango.");
    }
  }
}
