package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;
import org.junit.jupiter.api.Test;

class TipoMaterialSugeridoTest {

  @Test
  void resuelveValoresValidosDelCatalogo() {
    assertEquals(TipoMaterialSugerido.PET, TipoMaterialSugerido.desde("PET"));
    assertEquals(TipoMaterialSugerido.CARTON, TipoMaterialSugerido.desde("CARTON"));
    assertEquals(TipoMaterialSugerido.VIDRIO, TipoMaterialSugerido.desde("VIDRIO"));
    assertEquals(TipoMaterialSugerido.CHATARRA, TipoMaterialSugerido.desde("CHATARRA"));
  }

  @Test
  void rechazaTiposFueraDelCatalogo() {
    assertThrows(
        TipoMaterialSugeridoInvalidoException.class, () -> TipoMaterialSugerido.desde("MADERA"));
  }

  @Test
  void rechazaValorNulo() {
    assertThrows(
        TipoMaterialSugeridoInvalidoException.class, () -> TipoMaterialSugerido.desde(null));
  }

  @Test
  void cadaMaterialTieneUnPesoMaximoRazonableAcordeASuDensidad() {
    assertEquals(100.0, TipoMaterialSugerido.PET.pesoMaximoRazonableKg());
    assertEquals(200.0, TipoMaterialSugerido.CARTON.pesoMaximoRazonableKg());
    assertEquals(300.0, TipoMaterialSugerido.VIDRIO.pesoMaximoRazonableKg());
    assertEquals(500.0, TipoMaterialSugerido.CHATARRA.pesoMaximoRazonableKg());
  }
}
