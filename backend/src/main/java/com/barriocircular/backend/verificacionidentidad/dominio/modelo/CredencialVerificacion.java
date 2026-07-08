package com.barriocircular.backend.verificacionidentidad.dominio.modelo;

import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.CredencialYaRevocadaException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class CredencialVerificacion {

  private final UUID id;
  private final UUID perfilId;
  private final RolCredencial rolEmitido;
  private final TokenVerificacion token;
  private EstadoCredencial estado;
  private final Instant fechaEmision;
  private final Instant fechaExpiracion;
  private Instant fechaUltimaVerificacion;

  private CredencialVerificacion(
      UUID id,
      UUID perfilId,
      RolCredencial rolEmitido,
      TokenVerificacion token,
      EstadoCredencial estado,
      Instant fechaEmision,
      Instant fechaExpiracion,
      Instant fechaUltimaVerificacion) {
    this.id = Objects.requireNonNull(id, "El identificador de credencial es obligatorio");
    this.perfilId = Objects.requireNonNull(perfilId, "El perfil es obligatorio");
    this.rolEmitido = Objects.requireNonNull(rolEmitido, "El rol emitido es obligatorio");
    this.token = Objects.requireNonNull(token, "El token es obligatorio");
    this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    this.fechaEmision = Objects.requireNonNull(fechaEmision, "La fecha de emision es obligatoria");
    this.fechaExpiracion =
        Objects.requireNonNull(fechaExpiracion, "La fecha de expiracion es obligatoria");
    this.fechaUltimaVerificacion = fechaUltimaVerificacion;
  }

  public static CredencialVerificacion emitir(
      UUID perfilId, RolCredencial rolEmitido, TokenVerificacion token, int vigenciaEnDias) {
    if (vigenciaEnDias <= 0) {
      throw new IllegalArgumentException("La vigencia de la credencial debe ser positiva.");
    }
    Instant ahora = Instant.now();
    return new CredencialVerificacion(
        UUID.randomUUID(),
        perfilId,
        rolEmitido,
        token,
        EstadoCredencial.ACTIVA,
        ahora,
        ahora.plus(vigenciaEnDias, ChronoUnit.DAYS),
        null);
  }

  public static CredencialVerificacion reconstituir(
      UUID id,
      UUID perfilId,
      RolCredencial rolEmitido,
      TokenVerificacion token,
      EstadoCredencial estado,
      Instant fechaEmision,
      Instant fechaExpiracion,
      Instant fechaUltimaVerificacion) {
    return new CredencialVerificacion(
        id,
        perfilId,
        rolEmitido,
        token,
        estado,
        fechaEmision,
        fechaExpiracion,
        fechaUltimaVerificacion);
  }

  public void revocar() {
    if (estado == EstadoCredencial.REVOCADA) {
      throw new CredencialYaRevocadaException();
    }
    estado = EstadoCredencial.REVOCADA;
  }

  public boolean estaVigente(Instant ahora) {
    return estado == EstadoCredencial.ACTIVA && !fechaExpiracion.isBefore(ahora);
  }

  public void registrarVerificacion(Instant ahora) {
    fechaUltimaVerificacion = Objects.requireNonNull(ahora);
  }

  public UUID getId() {
    return id;
  }

  public UUID getPerfilId() {
    return perfilId;
  }

  public RolCredencial getRolEmitido() {
    return rolEmitido;
  }

  public TokenVerificacion getToken() {
    return token;
  }

  public EstadoCredencial getEstado() {
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
