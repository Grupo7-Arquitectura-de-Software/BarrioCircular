package com.barriocircular.backend.sugerenciaprecio.dominio.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import org.junit.jupiter.api.Test;

class CatalogoPreciosReferenciaTest {

  private final CatalogoPreciosReferencia catalogo = new CatalogoPreciosReferencia();

  @Test
  void devuelveElPrecioDeReferenciaParaCadaMaterialDelCatalogo() {
    assertEquals(PrecioSugerido.de(0.30), catalogo.precioDeReferencia(TipoMaterialSugerido.PET));
    assertEquals(PrecioSugerido.de(0.10), catalogo.precioDeReferencia(TipoMaterialSugerido.CARTON));
    assertEquals(PrecioSugerido.de(0.03), catalogo.precioDeReferencia(TipoMaterialSugerido.VIDRIO));
    assertEquals(
        PrecioSugerido.de(0.25), catalogo.precioDeReferencia(TipoMaterialSugerido.CHATARRA));
  }
}
