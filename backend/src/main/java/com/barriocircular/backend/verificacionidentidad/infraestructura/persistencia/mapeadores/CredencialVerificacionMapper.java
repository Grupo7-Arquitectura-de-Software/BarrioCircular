package com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.EstadoCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.RolCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.jpa.CredencialVerificacionEntity;
import org.springframework.stereotype.Component;

@Component
public class CredencialVerificacionMapper {

  public CredencialVerificacionEntity toEntity(CredencialVerificacion credencial) {
    return new CredencialVerificacionEntity(
        credencial.getId(),
        credencial.getPerfilId(),
        credencial.getRolEmitido().name(),
        credencial.getToken().valor(),
        credencial.getEstado().name(),
        credencial.getFechaEmision(),
        credencial.getFechaExpiracion(),
        credencial.getFechaUltimaVerificacion());
  }

  public CredencialVerificacion toDomain(CredencialVerificacionEntity entity) {
    return CredencialVerificacion.reconstituir(
        entity.getId(),
        entity.getPerfilId(),
        RolCredencial.valueOf(entity.getRolEmitido()),
        new TokenVerificacion(entity.getToken()),
        EstadoCredencial.valueOf(entity.getEstado()),
        entity.getFechaEmision(),
        entity.getFechaExpiracion(),
        entity.getFechaUltimaVerificacion());
  }
}
