package com.barriocircular.backend.perfiles.aplicacion.comandos;

import java.time.Instant;
import java.util.UUID;

public record RegistrarOnboardingPerfilPendienteCommand(
    UUID cuentaId, String clerkId, String correoElectronico, Instant ocurridoEn) {}
