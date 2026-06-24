package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barriocircular.backend.perfiles.aplicacion.comandos.RegistrarOnboardingPerfilPendienteCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;

@Service
public class RegistrarOnboardingPerfilPendienteUseCase {

    private static final String ESTADO_PENDIENTE_COMPLETAR = "PENDIENTE_COMPLETAR";

    private final PerfilOnboardingPendienteRepository onboardingPendienteRepository;

    public RegistrarOnboardingPerfilPendienteUseCase(
            PerfilOnboardingPendienteRepository onboardingPendienteRepository) {
        this.onboardingPendienteRepository = onboardingPendienteRepository;
    }

    @Transactional
    public void ejecutar(RegistrarOnboardingPerfilPendienteCommand command) {
        Objects.requireNonNull(command.cuentaId(), "El identificador de la cuenta es obligatorio");

        if (onboardingPendienteRepository.existePorCuentaId(command.cuentaId())) {
            return;
        }

        PerfilOnboardingPendiente onboardingPendiente = new PerfilOnboardingPendiente(
                UUID.randomUUID(),
                command.cuentaId(),
                command.clerkId(),
                command.correoElectronico(),
                ESTADO_PENDIENTE_COMPLETAR,
                command.ocurridoEn() != null ? command.ocurridoEn() : Instant.now());

        onboardingPendienteRepository.guardar(onboardingPendiente);
    }
}
