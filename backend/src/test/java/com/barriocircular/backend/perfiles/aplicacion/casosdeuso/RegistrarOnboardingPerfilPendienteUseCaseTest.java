package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.barriocircular.backend.perfiles.aplicacion.comandos.RegistrarOnboardingPerfilPendienteCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;

class RegistrarOnboardingPerfilPendienteUseCaseTest {

    @Test
    void registraOnboardingPendienteConEstadoPendienteCompletar() {
        PerfilOnboardingPendienteRepositoryFake repository = new PerfilOnboardingPendienteRepositoryFake();
        RegistrarOnboardingPerfilPendienteUseCase useCase = new RegistrarOnboardingPerfilPendienteUseCase(repository);
        UUID cuentaId = UUID.randomUUID();

        useCase.ejecutar(new RegistrarOnboardingPerfilPendienteCommand(
                cuentaId,
                "clerk-user-1",
                "ana@correo.com",
                Instant.parse("2026-06-24T10:00:00Z")));

        PerfilOnboardingPendiente onboarding = repository.onboardingPorCuenta.get(cuentaId);
        assertNotNull(onboarding);
        assertEquals(cuentaId, onboarding.cuentaId());
        assertEquals("PENDIENTE_COMPLETAR", onboarding.estado());
        assertEquals(1, repository.guardados);
    }

    @Test
    void noDuplicaOnboardingPendienteParaLaMismaCuenta() {
        PerfilOnboardingPendienteRepositoryFake repository = new PerfilOnboardingPendienteRepositoryFake();
        RegistrarOnboardingPerfilPendienteUseCase useCase = new RegistrarOnboardingPerfilPendienteUseCase(repository);
        UUID cuentaId = UUID.randomUUID();
        RegistrarOnboardingPerfilPendienteCommand command = new RegistrarOnboardingPerfilPendienteCommand(
                cuentaId,
                "clerk-user-1",
                "ana@correo.com",
                Instant.parse("2026-06-24T10:00:00Z"));

        useCase.ejecutar(command);
        useCase.ejecutar(command);

        assertEquals(1, repository.onboardingPorCuenta.size());
        assertEquals(1, repository.guardados);
    }

    private static final class PerfilOnboardingPendienteRepositoryFake
            implements PerfilOnboardingPendienteRepository {

        private final Map<UUID, PerfilOnboardingPendiente> onboardingPorCuenta = new HashMap<>();
        private int guardados;

        @Override
        public void guardar(PerfilOnboardingPendiente onboardingPendiente) {
            onboardingPorCuenta.put(onboardingPendiente.cuentaId(), onboardingPendiente);
            guardados++;
        }

        @Override
        public boolean existePorCuentaId(UUID cuentaId) {
            return onboardingPorCuenta.containsKey(cuentaId);
        }
    }
}
