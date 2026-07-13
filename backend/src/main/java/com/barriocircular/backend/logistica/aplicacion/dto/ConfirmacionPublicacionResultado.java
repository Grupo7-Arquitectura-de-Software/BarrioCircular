package com.barriocircular.backend.logistica.aplicacion.dto;

import java.util.UUID;

public record ConfirmacionPublicacionResultado(
    UUID publicacionId,
    String estadoPublicacion,
    Double pesoRealVerificado,
    String observacionesVerificacion) {}
