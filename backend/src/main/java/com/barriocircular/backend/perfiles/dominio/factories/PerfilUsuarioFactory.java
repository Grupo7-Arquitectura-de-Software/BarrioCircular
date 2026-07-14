package com.barriocircular.backend.perfiles.dominio.factories;

import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;
import com.barriocircular.backend.perfiles.dominio.excepciones.RolInvalidoException;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PerfilUsuarioFactory {

  private PerfilUsuarioFactory() {}

  public static PerfilUsuario crearPerfil(
      UUID cuentaUsuarioId,
      DocumentoIdentificacion documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      RolUsuario rol,
      InformacionContacto informacionContacto,
      CoordenadaGPS ubicacionHabitual) {
    if (cuentaUsuarioId == null) {
      throw new PerfilDomainException("El identificador de la cuenta de usuario es obligatorio");
    }
    if (rol == null) {
      throw new RolInvalidoException("Todo perfil debe tener un rol asignado desde su creacion");
    }

    return PerfilUsuario.crear(
        UUID.randomUUID(),
        cuentaUsuarioId,
        documentoIdentificacion,
        nombreCompleto,
        nombreComercial,
        rol,
        EstadoPerfil.ACTIVO,
        informacionContacto,
        ubicacionHabitual,
        LocalDateTime.now());
  }
}
