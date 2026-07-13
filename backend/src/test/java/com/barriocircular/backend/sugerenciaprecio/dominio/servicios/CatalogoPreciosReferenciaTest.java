package com.barriocircular.backend.sugerenciaprecio.dominio.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CatalogoPreciosReferenciaTest {

  private final CatalogoPreciosReferencia catalogo =
      new CatalogoPreciosReferencia(
          new BigDecimal("0.30"),
          new BigDecimal("0.10"),
          new BigDecimal("0.03"),
          new BigDecimal("0.25"));

  @Test
  void devuelveElPrecioDeReferenciaParaCadaMaterialDelCatalogo() {
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.30")),
        catalogo.precioDeReferencia(TipoMaterialSugerido.PET));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.10")),
        catalogo.precioDeReferencia(TipoMaterialSugerido.CARTON));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.03")),
        catalogo.precioDeReferencia(TipoMaterialSugerido.VIDRIO));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.25")),
        catalogo.precioDeReferencia(TipoMaterialSugerido.CHATARRA));
  }

  @Test
  void unMaterialEnEstadoExcelenteConservaElPrecioBase() {
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.30")),
        catalogo.precioSugerido(TipoMaterialSugerido.PET, EstadoMaterial.EXCELENTE));
  }

  @Test
  void ajustaElPrecioBaseConElFactorDelEstadoDelMaterial() {
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.27")),
        catalogo.precioSugerido(TipoMaterialSugerido.PET, EstadoMaterial.BUENO));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.24")),
        catalogo.precioSugerido(TipoMaterialSugerido.PET, EstadoMaterial.REGULAR));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.20")),
        catalogo.precioSugerido(TipoMaterialSugerido.CHATARRA, EstadoMaterial.REGULAR));
  }

  @Test
  void redondeaElPrecioAjustadoADosDecimales() {
    // 0.03 x 0.9 = 0.027 -> 0.03; 0.03 x 0.8 = 0.024 -> 0.02
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.03")),
        catalogo.precioSugerido(TipoMaterialSugerido.VIDRIO, EstadoMaterial.BUENO));
    assertEquals(
        new PrecioSugerido(new BigDecimal("0.02")),
        catalogo.precioSugerido(TipoMaterialSugerido.VIDRIO, EstadoMaterial.REGULAR));
  }
}
