package com.barriocircular.backend.sugerenciaprecio.aplicacion.dto;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.FuenteSugerencia;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SugerenciaPrecioResultado(
    UUID resultadoId,
    BigDecimal precioSugeridoPorKilo,
    FuenteSugerencia fuente,
    String justificacion,
    Instant fechaSugerencia) {

  public static SugerenciaPrecioResultado desde(SugerenciaPrecio sugerenciaPrecio) {
    return new SugerenciaPrecioResultado(
        sugerenciaPrecio.id().valor(),
        sugerenciaPrecio.precioSugerido().valor(),
        sugerenciaPrecio.fuente(),
        sugerenciaPrecio.justificacion(),
        sugerenciaPrecio.fechaSugerencia());
  }
}
