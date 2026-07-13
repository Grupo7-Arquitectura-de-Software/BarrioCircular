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
    Double pesoRealVerificado,
    String observacionesVerificacion,
    String nombreCreador,
    String telefonoCreador) {

  public PublicacionResultado(
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
      UUID reservadoPor) {
    this(
        publicacionId,
        creadorId,
        tipoResiduo,
        pesoKg,
        precioPorKilo,
        latitud,
        longitud,
        evidenciaUrl,
        estado,
        fechaCreacion,
        reservadoPor,
        null,
        null,
        null,
        null);
  }

  public PublicacionResultado(
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
    this(
        publicacionId,
        creadorId,
        tipoResiduo,
        pesoKg,
        precioPorKilo,
        latitud,
        longitud,
        evidenciaUrl,
        estado,
        fechaCreacion,
        reservadoPor,
        null,
        null,
        nombreCreador,
        telefonoCreador);
  }

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
        publicacion.pesoRealVerificado(),
        publicacion.observacionesVerificacion(),
        nombreCreador,
        telefonoCreador);
  }
}
