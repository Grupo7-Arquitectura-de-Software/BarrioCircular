package com.barriocircular.backend.perfiles.aplicacion.dto;

import java.time.Instant;
import java.util.UUID;

public record PerfilOnboardingPendiente(
        UUID id,
        UUID cuentaId,
        String clerkId,
        String correoElectronico,
        String estado,
        Instant fechaRegistro) {
}
