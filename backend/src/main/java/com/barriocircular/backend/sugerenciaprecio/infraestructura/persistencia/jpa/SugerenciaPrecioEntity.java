package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sugerencias_precio")
public class SugerenciaPrecioEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 40)
  private String tipoMaterial;

  private Double pesoKg;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioSugerido;

  @Column(nullable = false, length = 40)
  private String fuente;

  @Column(length = 500)
  private String justificacion;

  private String solicitanteClerkId;

  @Column(nullable = false)
  private Instant fechaSugerencia;

  protected SugerenciaPrecioEntity() {}

  public SugerenciaPrecioEntity(
      UUID id,
      String tipoMaterial,
      Double pesoKg,
      BigDecimal precioSugerido,
      String fuente,
      String justificacion,
      String solicitanteClerkId,
      Instant fechaSugerencia) {
    this.id = id;
    this.tipoMaterial = tipoMaterial;
    this.pesoKg = pesoKg;
    this.precioSugerido = precioSugerido;
    this.fuente = fuente;
    this.justificacion = justificacion;
    this.solicitanteClerkId = solicitanteClerkId;
    this.fechaSugerencia = fechaSugerencia;
  }

  public UUID getId() {
    return id;
  }

  public String getTipoMaterial() {
    return tipoMaterial;
  }

  public Double getPesoKg() {
    return pesoKg;
  }

  public BigDecimal getPrecioSugerido() {
    return precioSugerido;
  }

  public String getFuente() {
    return fuente;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public String getSolicitanteClerkId() {
    return solicitanteClerkId;
  }

  public Instant getFechaSugerencia() {
    return fechaSugerencia;
  }
}
