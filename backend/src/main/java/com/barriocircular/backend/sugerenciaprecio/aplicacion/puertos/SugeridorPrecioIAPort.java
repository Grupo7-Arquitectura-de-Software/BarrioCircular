package com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaIA;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.util.Optional;

/**
 * Contrato importante: las implementaciones nunca deben propagar excepciones por fallos de
 * red/timeout/parseo del proveedor de IA — deben devolver {@link Optional#empty()} en esos casos.
 * Un fallo del proveedor no es una condición excepcional para este dominio, es un camino esperado
 * que activa el catálogo de respaldo.
 */
public interface SugeridorPrecioIAPort {

  Optional<SugerenciaIA> sugerirPrecio(
      TipoMaterialSugerido tipoMaterial, Double pesoKg, String imagenBase64);
}
