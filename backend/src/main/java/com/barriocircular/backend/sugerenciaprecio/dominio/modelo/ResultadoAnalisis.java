package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

/**
 * Veredicto del análisis de la foto de un material. Solo {@link #VALIDO} habilita autocompletar la
 * publicación; los demás resultados le piden al usuario otra foto o continuar manualmente.
 */
public enum ResultadoAnalisis {
  VALIDO,
  NO_ES_RECICLAJE,
  FOTO_NO_CLARA,
  MULTIPLES_MATERIALES,
  MATERIAL_NO_SOPORTADO,
  IA_NO_DISPONIBLE
}
