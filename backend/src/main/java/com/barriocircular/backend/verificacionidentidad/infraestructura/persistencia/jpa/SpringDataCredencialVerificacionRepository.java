package com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCredencialVerificacionRepository
    extends JpaRepository<CredencialVerificacionEntity, UUID> {

  Optional<CredencialVerificacionEntity> findByToken(String token);

  Optional<CredencialVerificacionEntity> findFirstByPerfilIdAndEstadoOrderByFechaEmisionDesc(
      UUID perfilId, String estado);

  List<CredencialVerificacionEntity> findByPerfilIdOrderByFechaEmisionDesc(UUID perfilId);
}
