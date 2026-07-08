package com.barriocircular.backend.sugerenciaprecio.aplicacion.dto;

import java.math.BigDecimal;

/**
 * {@code materialCoincide} indica si, a criterio del modelo, la foto adjunta corresponde al tipo de
 * material declarado. Cuando no se envía foto, siempre es {@code true} (no aplica la validación
 * visual).
 */
public record SugerenciaIA(
    BigDecimal precioPorKilo, String justificacion, boolean materialCoincide) {}
