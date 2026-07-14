package com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "perfiles_onboarding_pendiente")
public class PerfilOnboardingPendienteEntity {

  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private UUID cuentaId;

  @Column(length = 120)
  private String clerkId;

  @Column(length = 180)
  private String correoElectronico;

  @Column(nullable = false, length = 40)
  private String estado;

  @Column(nullable = false)
  private Instant fechaRegistro;

  protected PerfilOnboardingPendienteEntity() {}

  public PerfilOnboardingPendienteEntity(
      UUID id,
      UUID cuentaId,
      String clerkId,
      String correoElectronico,
      String estado,
      Instant fechaRegistro) {
    this.id = id;
    this.cuentaId = cuentaId;
    this.clerkId = clerkId;
    this.correoElectronico = correoElectronico;
    this.estado = estado;
    this.fechaRegistro = fechaRegistro;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCuentaId() {
    return cuentaId;
  }

  public String getClerkId() {
    return clerkId;
  }

  public String getCorreoElectronico() {
    return correoElectronico;
  }

  public String getEstado() {
    return estado;
  }

  public Instant getFechaRegistro() {
    return fechaRegistro;
  }
}
