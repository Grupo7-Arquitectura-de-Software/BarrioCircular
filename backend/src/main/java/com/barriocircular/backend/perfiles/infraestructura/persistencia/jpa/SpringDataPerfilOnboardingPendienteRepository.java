package com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPerfilOnboardingPendienteRepository
    extends JpaRepository<PerfilOnboardingPendienteEntity, UUID> {

  boolean existsByCuentaId(UUID cuentaId);

  Optional<PerfilOnboardingPendienteEntity> findByCuentaId(UUID cuentaId);
}
