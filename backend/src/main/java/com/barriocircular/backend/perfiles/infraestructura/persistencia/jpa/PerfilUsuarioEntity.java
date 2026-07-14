package com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "perfiles_usuario")
public class PerfilUsuarioEntity {

  @Id private UUID id;

  @Column(nullable = false, unique = true)
  private UUID cuentaUsuarioId;

  @Column(nullable = false, unique = true, length = 13)
  private String documentoIdentificacion;

  @Column(length = 160)
  private String nombreCompleto;

  @Column(length = 160)
  private String nombreComercial;

  @Column(nullable = false, length = 40)
  private String rol;

  @Column(nullable = false, length = 40)
  private String estadoPerfil;

  @Column(nullable = false, length = 180)
  private String correoElectronico;

  @Column(nullable = false, length = 20)
  private String telefono;

  @Column(nullable = false)
  private double latitud;

  @Column(nullable = false)
  private double longitud;

  @Column(length = 240)
  private String direccionHabitual;

  @Column(nullable = false)
  private LocalDateTime fechaCreacion;

  protected PerfilUsuarioEntity() {}

  public PerfilUsuarioEntity(
      UUID id,
      UUID cuentaUsuarioId,
      String documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      String rol,
      String estadoPerfil,
      String correoElectronico,
      String telefono,
      double latitud,
      double longitud,
      LocalDateTime fechaCreacion) {
    this(
        id,
        cuentaUsuarioId,
        documentoIdentificacion,
        nombreCompleto,
        nombreComercial,
        rol,
        estadoPerfil,
        correoElectronico,
        telefono,
        latitud,
        longitud,
        null,
        fechaCreacion);
  }

  public PerfilUsuarioEntity(
      UUID id,
      UUID cuentaUsuarioId,
      String documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      String rol,
      String estadoPerfil,
      String correoElectronico,
      String telefono,
      double latitud,
      double longitud,
      String direccionHabitual,
      LocalDateTime fechaCreacion) {
    this.id = id;
    this.cuentaUsuarioId = cuentaUsuarioId;
    this.documentoIdentificacion = documentoIdentificacion;
    this.nombreCompleto = nombreCompleto;
    this.nombreComercial = nombreComercial;
    this.rol = rol;
    this.estadoPerfil = estadoPerfil;
    this.correoElectronico = correoElectronico;
    this.telefono = telefono;
    this.latitud = latitud;
    this.longitud = longitud;
    this.direccionHabitual = direccionHabitual;
    this.fechaCreacion = fechaCreacion;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCuentaUsuarioId() {
    return cuentaUsuarioId;
  }

  public String getDocumentoIdentificacion() {
    return documentoIdentificacion;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public String getNombreComercial() {
    return nombreComercial;
  }

  public String getRol() {
    return rol;
  }

  public String getEstadoPerfil() {
    return estadoPerfil;
  }

  public String getCorreoElectronico() {
    return correoElectronico;
  }

  public String getTelefono() {
    return telefono;
  }

  public double getLatitud() {
    return latitud;
  }

  public double getLongitud() {
    return longitud;
  }

  public String getDireccionHabitual() {
    return direccionHabitual;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }
}
