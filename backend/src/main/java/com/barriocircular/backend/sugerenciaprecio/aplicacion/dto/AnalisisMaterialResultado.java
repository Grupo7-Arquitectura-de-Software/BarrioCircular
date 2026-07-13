package com.barriocircular.backend.sugerenciaprecio.aplicacion.dto;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.ResultadoAnalisis;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AnalisisMaterialResultado(
    UUID analisisId,
    ResultadoAnalisis resultado,
    TipoMaterialSugerido tipoMaterial,
    Double pesoEstimadoKg,
    EstadoMaterial estadoMaterial,
    BigDecimal precioSugeridoPorKilo,
    String recomendacion,
    Instant fechaAnalisis) {

  public static AnalisisMaterialResultado desde(AnalisisMaterial analisis) {
    return new AnalisisMaterialResultado(
        analisis.id().valor(),
        analisis.resultado(),
        analisis.tipoMaterial(),
        analisis.pesoEstimadoKg(),
        analisis.estadoMaterial(),
        analisis.precioSugerido() == null ? null : analisis.precioSugerido().valor(),
        analisis.recomendacion(),
        analisis.fechaAnalisis());
  }
}
