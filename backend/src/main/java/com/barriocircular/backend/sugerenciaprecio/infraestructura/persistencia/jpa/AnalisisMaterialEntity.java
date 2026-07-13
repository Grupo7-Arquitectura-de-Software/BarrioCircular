package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "analisis_material")
public class AnalisisMaterialEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 40)
  private String resultado;

  @Column(length = 40)
  private String tipoMaterial;

  private Double pesoEstimadoKg;

  @Column(length = 40)
  private String estadoMaterial;

  @Column(precision = 12, scale = 2)
  private BigDecimal precioSugerido;

  @Column(length = 500)
  private String recomendacion;

  private String solicitanteClerkId;

  @Column(nullable = false)
  private Instant fechaAnalisis;

  protected AnalisisMaterialEntity() {}

  public AnalisisMaterialEntity(
      UUID id,
      String resultado,
      String tipoMaterial,
      Double pesoEstimadoKg,
      String estadoMaterial,
      BigDecimal precioSugerido,
      String recomendacion,
      String solicitanteClerkId,
      Instant fechaAnalisis) {
    this.id = id;
    this.resultado = resultado;
    this.tipoMaterial = tipoMaterial;
    this.pesoEstimadoKg = pesoEstimadoKg;
    this.estadoMaterial = estadoMaterial;
    this.precioSugerido = precioSugerido;
    this.recomendacion = recomendacion;
    this.solicitanteClerkId = solicitanteClerkId;
    this.fechaAnalisis = fechaAnalisis;
  }

  public UUID getId() {
    return id;
  }

  public String getResultado() {
    return resultado;
  }

  public String getTipoMaterial() {
    return tipoMaterial;
  }

  public Double getPesoEstimadoKg() {
    return pesoEstimadoKg;
  }

  public String getEstadoMaterial() {
    return estadoMaterial;
  }

  public BigDecimal getPrecioSugerido() {
    return precioSugerido;
  }

  public String getRecomendacion() {
    return recomendacion;
  }

  public String getSolicitanteClerkId() {
    return solicitanteClerkId;
  }

  public Instant getFechaAnalisis() {
    return fechaAnalisis;
  }
}
