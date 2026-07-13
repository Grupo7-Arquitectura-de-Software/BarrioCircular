package com.barriocircular.backend.sugerenciaprecio.aplicacion.dto;

/**
 * Respuesta cruda del modelo de visión, sin validar: los campos llegan tal cual los produjo la IA y
 * es el caso de uso quien decide qué valores son confiables (tipos del catálogo, peso en rango,
 * estado reconocido).
 */
public record AnalisisIA(
    Boolean esMaterialReciclaje,
    Boolean fotoClara,
    Boolean multiplesMateriales,
    String tipoMaterial,
    Double pesoEstimadoKg,
    String estadoMaterial,
    String recomendacion) {}
