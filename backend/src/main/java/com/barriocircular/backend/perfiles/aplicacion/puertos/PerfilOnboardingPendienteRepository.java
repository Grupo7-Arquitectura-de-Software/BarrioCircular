package com.barriocircular.backend.perfiles.aplicacion.puertos;

import java.util.UUID;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;

public interface PerfilOnboardingPendienteRepository {

    void guardar(PerfilOnboardingPendiente onboardingPendiente);

    boolean existePorCuentaId(UUID cuentaId);
}
