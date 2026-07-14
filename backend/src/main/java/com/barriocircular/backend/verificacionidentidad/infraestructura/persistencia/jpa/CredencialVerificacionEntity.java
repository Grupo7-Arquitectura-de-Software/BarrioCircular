package com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "credenciales_verificacion",
    indexes =
        @Index(name = "idx_credenciales_verificacion_token", columnList = "token", unique = true))
public class CredencialVerificacionEntity {

  @Id private UUID id;

  @Column(name = "perfil_id", nullable = false)
  private UUID perfilId;

  @Column(name = "rol_emitido", nullable = false, length = 40)
  private String rolEmitido;

  @Column(nullable = false, unique = true, length = 120)
  private String token;

  @Column(nullable = false, length = 40)
  private String estado;

  @Column(name = "fecha_emision", nullable = false)
  private Instant fechaEmision;

  @Column(name = "fecha_expiracion", nullable = false)
  private Instant fechaExpiracion;

  @Column(name = "fecha_ultima_verificacion")
  private Instant fechaUltimaVerificacion;

  protected CredencialVerificacionEntity() {}

  public CredencialVerificacionEntity(
      UUID id,
      UUID perfilId,
      String rolEmitido,
      String token,
      String estado,
      Instant fechaEmision,
      Instant fechaExpiracion,
      Instant fechaUltimaVerificacion) {
    this.id = id;
    this.perfilId = perfilId;
    this.rolEmitido = rolEmitido;
    this.token = token;
    this.estado = estado;
    this.fechaEmision = fechaEmision;
    this.fechaExpiracion = fechaExpiracion;
    this.fechaUltimaVerificacion = fechaUltimaVerificacion;
  }

  public UUID getId() {
    return id;
  }

  public UUID getPerfilId() {
    return perfilId;
  }

  public String getRolEmitido() {
    return rolEmitido;
  }

  public String getToken() {
    return token;
  }

  public String getEstado() {
    return estado;
  }

  public Instant getFechaEmision() {
    return fechaEmision;
  }

  public Instant getFechaExpiracion() {
    return fechaExpiracion;
  }

  public Instant getFechaUltimaVerificacion() {
    return fechaUltimaVerificacion;
  }
}
