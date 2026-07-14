package com.barriocircular.backend.perfiles.aplicacion.mapeadores;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;

public final class PerfilResultadoMapper {

  private PerfilResultadoMapper() {}

  public static PerfilResultado desde(PerfilUsuario perfilUsuario) {
    return new PerfilResultado(
        perfilUsuario.getId(),
        perfilUsuario.getCuentaUsuarioId(),
        perfilUsuario.getDocumentoIdentificacion().getValor(),
        perfilUsuario.getNombreCompleto(),
        perfilUsuario.getNombreComercial(),
        perfilUsuario.getRol().name(),
        perfilUsuario.getEstadoPerfil().name(),
        perfilUsuario.getInformacionContacto().getCorreoElectronico(),
        perfilUsuario.getInformacionContacto().getTelefono(),
        perfilUsuario.getUbicacionHabitual().getLatitud(),
        perfilUsuario.getUbicacionHabitual().getLongitud(),
        perfilUsuario.getDireccionHabitual());
  }
}
