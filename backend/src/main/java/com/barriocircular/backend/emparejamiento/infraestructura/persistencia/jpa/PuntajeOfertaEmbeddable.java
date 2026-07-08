package com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class PuntajeOfertaEmbeddable {

  @Column(name = "publicacion_id", nullable = false)
  private UUID publicacionId;

  @Column(name = "distancia_km", nullable = false)
  private double distanciaKm;

  @Column(name = "precio_kg", nullable = false, precision = 12, scale = 2)
  private BigDecimal precioKg;

  @Column(name = "score_total", nullable = false)
  private double scoreTotal;

  protected PuntajeOfertaEmbeddable() {}

  public PuntajeOfertaEmbeddable(
      UUID publicacionId, double distanciaKm, BigDecimal precioKg, double scoreTotal) {
    this.publicacionId = publicacionId;
    this.distanciaKm = distanciaKm;
    this.precioKg = precioKg;
    this.scoreTotal = scoreTotal;
  }

  public UUID getPublicacionId() {
    return publicacionId;
  }

  public double getDistanciaKm() {
    return distanciaKm;
  }

  public BigDecimal getPrecioKg() {
    return precioKg;
  }

  public double getScoreTotal() {
    return scoreTotal;
  }
}
