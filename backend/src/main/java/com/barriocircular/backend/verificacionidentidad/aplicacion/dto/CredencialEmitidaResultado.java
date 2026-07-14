package com.barriocircular.backend.verificacionidentidad.aplicacion.dto;

import java.time.Instant;
import java.util.UUID;

public record CredencialEmitidaResultado(
    UUID credencialId, String urlVerificacion, Instant fechaEmision, Instant fechaExpiracion) {}
