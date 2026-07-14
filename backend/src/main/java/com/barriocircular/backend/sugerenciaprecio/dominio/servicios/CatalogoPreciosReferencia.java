package com.barriocircular.backend.sugerenciaprecio.dominio.servicios;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Precios base de mercado por material (USD/kg). Los valores llegan por constructor desde la
 * configuración (variables de entorno PRECIO_KG_*), de modo que el precio sugerido sea determinista
 * y ajustable sin recompilar: la IA nunca decide el precio, solo el estado del material.
 */
public class CatalogoPreciosReferencia {

  private final Map<TipoMaterialSugerido, PrecioSugerido> preciosDeReferencia;

  public CatalogoPreciosReferencia(
      BigDecimal precioPet,
      BigDecimal precioCarton,
      BigDecimal precioVidrio,
      BigDecimal precioChatarra) {
    this.preciosDeReferencia =
        Map.of(
            TipoMaterialSugerido.PET, new PrecioSugerido(precioPet),
            TipoMaterialSugerido.CARTON, new PrecioSugerido(precioCarton),
            TipoMaterialSugerido.VIDRIO, new PrecioSugerido(precioVidrio),
            TipoMaterialSugerido.CHATARRA, new PrecioSugerido(precioChatarra));
  }

  public PrecioSugerido precioDeReferencia(TipoMaterialSugerido tipoMaterial) {
    return preciosDeReferencia.get(tipoMaterial);
  }

  /**
   * Precio base del material ajustado por su estado de conservación: base × factor del estado,
   * redondeado a centavos.
   */
  public PrecioSugerido precioSugerido(
      TipoMaterialSugerido tipoMaterial, EstadoMaterial estadoMaterial) {
    BigDecimal precioBase = precioDeReferencia(tipoMaterial).valor();
    BigDecimal precioAjustado =
        precioBase.multiply(estadoMaterial.factor()).setScale(2, RoundingMode.HALF_UP);
    return new PrecioSugerido(precioAjustado);
  }
}
