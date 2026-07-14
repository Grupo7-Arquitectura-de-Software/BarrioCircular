package com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisIA;
import java.util.Optional;

/**
 * Contrato importante: las implementaciones nunca deben propagar excepciones por fallos de
 * red/timeout/parseo del proveedor de IA — deben devolver {@link Optional#empty()} en esos casos.
 * Un fallo del proveedor no es una condición excepcional para este dominio, es un camino esperado
 * que produce un análisis IA_NO_DISPONIBLE y deja al usuario continuar manualmente.
 */
public interface AnalizadorMaterialIAPort {

  Optional<AnalisisIA> analizar(String imagenBase64);
}
