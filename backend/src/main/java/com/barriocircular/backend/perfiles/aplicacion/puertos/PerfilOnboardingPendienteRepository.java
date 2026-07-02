package com.barriocircular.backend.perfiles.aplicacion.puertos;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import java.util.UUID;

public interface PerfilOnboardingPendienteRepository {

  void guardar(PerfilOnboardingPendiente onboardingPendiente);

  boolean existePorCuentaId(UUID cuentaId);

  void eliminarPorCuentaId(UUID cuentaId);
}
