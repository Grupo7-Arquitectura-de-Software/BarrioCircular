package com.barriocircular.backend.logistica.aplicacion.dto;

import java.util.UUID;

public record ConfirmacionRecoleccionResultado(
    UUID rutaId,
    String estadoRuta,
    UUID paradaId,
    String estadoParada,
    UUID publicacionId,
    String estadoPublicacion,
    Double pesoRealVerificado,
    boolean rutaTerminada) {}
