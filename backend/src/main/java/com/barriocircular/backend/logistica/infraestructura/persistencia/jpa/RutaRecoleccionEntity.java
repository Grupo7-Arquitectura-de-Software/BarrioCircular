package com.barriocircular.backend.logistica.infraestructura.persistencia.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Version;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ruta_recoleccion")
public class RutaRecoleccionEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID recicladorId;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = false)
  private LocalTime horaInicio;

  @Column(nullable = false, length = 40)
  private String estado;

  @Column(nullable = false)
  private Instant fechaCreacion;

  @Version
  private Long version;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "ruta_id", nullable = false)
  @OrderBy("orden ASC")
  private List<ParadaRecoleccionEntity> paradas = new ArrayList<>();

  protected RutaRecoleccionEntity() {}

  public RutaRecoleccionEntity(
      UUID id,
      UUID recicladorId,
      LocalDate fecha,
      LocalTime horaInicio,
      String estado,
      Instant fechaCreacion,
      List<ParadaRecoleccionEntity> paradas) {
    this.id = id;
    this.recicladorId = recicladorId;
    this.fecha = fecha;
    this.horaInicio = horaInicio;
    this.estado = estado;
    this.fechaCreacion = fechaCreacion;
    this.paradas = new ArrayList<>(paradas);
  }

  public UUID getId() {
    return id;
  }

  public UUID getRecicladorId() {
    return recicladorId;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public String getEstado() {
    return estado;
  }

  public Instant getFechaCreacion() {
    return fechaCreacion;
  }

  public List<ParadaRecoleccionEntity> getParadas() {
    return paradas;
  }
}
