package com.barriocircular.backend.sugerenciaprecio.dominio.servicios;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.util.Map;

/**
 * Piso de seguridad cuando GroQ no responde o responde algo fuera de rango. Valores ilustrativos,
 * no datos de mercado verificados — deben confirmarse o ajustarse antes de considerar esta tabla
 * "de producción".
 */
public class CatalogoPreciosReferencia {

  private static final Map<TipoMaterialSugerido, PrecioSugerido> PRECIOS_DE_REFERENCIA =
      Map.of(
          TipoMaterialSugerido.PET, PrecioSugerido.de(0.30),
          TipoMaterialSugerido.CARTON, PrecioSugerido.de(0.10),
          TipoMaterialSugerido.VIDRIO, PrecioSugerido.de(0.03),
          TipoMaterialSugerido.CHATARRA, PrecioSugerido.de(0.25));

  public PrecioSugerido precioDeReferencia(TipoMaterialSugerido tipoMaterial) {
    return PRECIOS_DE_REFERENCIA.get(tipoMaterial);
  }
}
