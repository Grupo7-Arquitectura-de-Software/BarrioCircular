package com.barriocircular.backend.acceso.infraestructura.persistencia.jpa;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuentas_acceso")
@Getter
@Setter
@NoArgsConstructor
public class CuentaAccesoEntity {
  @Id private UUID identificadorCuenta;

  @Column(nullable = false)
  private String identificadorUsuarioClerk;

  @Column(nullable = false)
  private String correoElectronico;

  @Column(nullable = false)
  private String estadoSesion;

  public CuentaAccesoEntity(
      UUID identificadorCuenta,
      String identificadorUsuarioClerk,
      String correoElectronico,
      String estadoSesion) {
    this.identificadorCuenta = identificadorCuenta;
    this.identificadorUsuarioClerk = identificadorUsuarioClerk;
    this.correoElectronico = correoElectronico;
    this.estadoSesion = estadoSesion;
  }
}
