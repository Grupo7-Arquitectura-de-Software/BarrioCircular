package com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "publicaciones")
public class PublicacionEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID creadorId;

  @Column(nullable = false, length = 40)
  private String tipoResiduo;

  @Column(nullable = false)
  private double pesoKg;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioPorKilo;

  @Column(nullable = false)
  private double latitud;

  @Column(nullable = false)
  private double longitud;

  @Column(nullable = false, length = 500)
  private String evidenciaUrl;

  @Column(nullable = false)
  private Instant fechaCreacion;

  @Column(nullable = false, length = 40)
  private String estado;

  @Column(name = "reservado_por")
  private UUID reservadoPor;

  private Double pesoRealVerificado;

  @Column(length = 1000)
  private String observacionesVerificacion;

  protected PublicacionEntity() {}

  public PublicacionEntity(
      UUID id,
      UUID creadorId,
      String tipoResiduo,
      double pesoKg,
      BigDecimal precioPorKilo,
      double latitud,
      double longitud,
      String evidenciaUrl,
      Instant fechaCreacion,
      String estado,
      UUID reservadoPor,
      Double pesoRealVerificado,
      String observacionesVerificacion) {
    this.id = id;
    this.creadorId = creadorId;
    this.tipoResiduo = tipoResiduo;
    this.pesoKg = pesoKg;
    this.precioPorKilo = precioPorKilo;
    this.latitud = latitud;
    this.longitud = longitud;
    this.evidenciaUrl = evidenciaUrl;
    this.fechaCreacion = fechaCreacion;
    this.estado = estado;
    this.reservadoPor = reservadoPor;
    this.pesoRealVerificado = pesoRealVerificado;
    this.observacionesVerificacion = observacionesVerificacion;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCreadorId() {
    return creadorId;
  }

  public String getTipoResiduo() {
    return tipoResiduo;
  }

  public double getPesoKg() {
    return pesoKg;
  }

  public BigDecimal getPrecioPorKilo() {
    return precioPorKilo;
  }

  public double getLatitud() {
    return latitud;
  }

  public double getLongitud() {
    return longitud;
  }

  public String getEvidenciaUrl() {
    return evidenciaUrl;
  }

  public Instant getFechaCreacion() {
    return fechaCreacion;
  }

  public String getEstado() {
    return estado;
  }

  public UUID getReservadoPor() {
    return reservadoPor;
  }

  public Double getPesoRealVerificado() {
    return pesoRealVerificado;
  }

  public String getObservacionesVerificacion() {
    return observacionesVerificacion;
  }
}
