package com.barriocircular.backend.publicacion.aplicacion.dto;

import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PublicacionResultado(
    UUID publicacionId,
    UUID creadorId,
    String tipoResiduo,
    double pesoKg,
    BigDecimal precioPorKilo,
    double latitud,
    double longitud,
    String evidenciaUrl,
    String estado,
    Instant fechaCreacion,
    UUID reservadoPor,
    String nombreCreador,
    String telefonoCreador) {

  public static PublicacionResultado desde(Publicacion publicacion) {
    return desde(publicacion, null, null);
  }

  public static PublicacionResultado desde(
      Publicacion publicacion, String nombreCreador, String telefonoCreador) {
    return new PublicacionResultado(
        publicacion.id().valor(),
        publicacion.creador().valor(),
        publicacion.detalle().tipo().name(),
        publicacion.detalle().peso().valorKg(),
        publicacion.precioPorKilo().valor(),
        publicacion.ubicacion().latitud(),
        publicacion.ubicacion().longitud(),
        publicacion.evidencia().url(),
        publicacion.estado().name(),
        publicacion.fechaCreacion(),
        publicacion.reservadoPor() == null ? null : publicacion.reservadoPor().valor(),
        nombreCreador,
        telefonoCreador);
  }
}
