package com.barriocircular.backend.verificacionidentidad.aplicacion.puertos;

import java.time.Instant;
import java.util.UUID;

public record PerfilVerificable(
    UUID perfilId,
    String nombreMostrado,
    String rol,
    boolean perfilActivo,
    boolean cuentaActiva,
    Instant fechaRegistro) {}
