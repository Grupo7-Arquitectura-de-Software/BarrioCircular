package com.barriocircular.backend.perfiles.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.PerfilUsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class PerfilUsuarioMapper {

  public PerfilUsuarioEntity toEntity(PerfilUsuario perfil) {
    return new PerfilUsuarioEntity(
        perfil.getId(),
        perfil.getCuentaUsuarioId(),
        perfil.getDocumentoIdentificacion().getValor(),
        perfil.getNombreCompleto(),
        perfil.getNombreComercial(),
        perfil.getRol().name(),
        perfil.getEstadoPerfil().name(),
        perfil.getInformacionContacto().getCorreoElectronico(),
        perfil.getInformacionContacto().getTelefono(),
        perfil.getUbicacionHabitual().getLatitud(),
        perfil.getUbicacionHabitual().getLongitud(),
        perfil.getFechaCreacion());
  }

  public PerfilUsuario toDomain(PerfilUsuarioEntity entity) {
    return PerfilUsuario.reconstituir(
        entity.getId(),
        entity.getCuentaUsuarioId(),
        new DocumentoIdentificacion(entity.getDocumentoIdentificacion()),
        entity.getNombreCompleto(),
        entity.getNombreComercial(),
        RolUsuario.valueOf(entity.getRol()),
        EstadoPerfil.valueOf(entity.getEstadoPerfil()),
        new InformacionContacto(entity.getCorreoElectronico(), entity.getTelefono()),
        new CoordenadaGPS(entity.getLatitud(), entity.getLongitud()),
        entity.getFechaCreacion());
  }
}
