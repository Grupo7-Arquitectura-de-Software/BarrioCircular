package com.barriocircular.backend.logistica.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "parada_recoleccion")
public class ParadaRecoleccionEntity {

  @Id private UUID id;

  @Column(name = "ruta_id", nullable = false, insertable = false, updatable = false)
  private UUID rutaId;

  @Column(nullable = false)
  private UUID publicacionId;

  @Column(nullable = false)
  private int orden;

  @Column(nullable = false)
  private double latitud;

  @Column(nullable = false)
  private double longitud;

  @Column(nullable = false)
  private ZonedDateTime horaLlegadaEstimada;

  private ZonedDateTime horaLlegadaReal;

  @Column(nullable = false, length = 40)
  private String estado;

  protected ParadaRecoleccionEntity() {}

  public ParadaRecoleccionEntity(
      UUID id,
      UUID rutaId,
      UUID publicacionId,
      int orden,
      double latitud,
      double longitud,
      ZonedDateTime horaLlegadaEstimada,
      ZonedDateTime horaLlegadaReal,
      String estado) {
    this.id = id;
    this.rutaId = rutaId;
    this.publicacionId = publicacionId;
    this.orden = orden;
    this.latitud = latitud;
    this.longitud = longitud;
    this.horaLlegadaEstimada = horaLlegadaEstimada;
    this.horaLlegadaReal = horaLlegadaReal;
    this.estado = estado;
  }

  public UUID getId() {
    return id;
  }

  public UUID getRutaId() {
    return rutaId;
  }

  public UUID getPublicacionId() {
    return publicacionId;
  }

  public int getOrden() {
    return orden;
  }

  public double getLatitud() {
    return latitud;
  }

  public double getLongitud() {
    return longitud;
  }

  public ZonedDateTime getHoraLlegadaEstimada() {
    return horaLlegadaEstimada;
  }

  public ZonedDateTime getHoraLlegadaReal() {
    return horaLlegadaReal;
  }

  public String getEstado() {
    return estado;
  }
}
