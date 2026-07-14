package com.barriocircular.backend.perfiles.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.PerfilOnboardingPendienteEntity;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilOnboardingPendienteRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilOnboardingPendienteRepositoryAdapter
    implements PerfilOnboardingPendienteRepository {

  private final SpringDataPerfilOnboardingPendienteRepository springDataRepository;

  public PerfilOnboardingPendienteRepositoryAdapter(
      SpringDataPerfilOnboardingPendienteRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void guardar(PerfilOnboardingPendiente onboardingPendiente) {
    springDataRepository.save(
        new PerfilOnboardingPendienteEntity(
            onboardingPendiente.id(),
            onboardingPendiente.cuentaId(),
            onboardingPendiente.clerkId(),
            onboardingPendiente.correoElectronico(),
            onboardingPendiente.estado(),
            onboardingPendiente.fechaRegistro()));
  }

  @Override
  public boolean existePorCuentaId(UUID cuentaId) {
    return springDataRepository.existsByCuentaId(cuentaId);
  }

  @Override
  public void eliminarPorCuentaId(UUID cuentaId) {
    springDataRepository.findByCuentaId(cuentaId).ifPresent(springDataRepository::delete);
  }
}
