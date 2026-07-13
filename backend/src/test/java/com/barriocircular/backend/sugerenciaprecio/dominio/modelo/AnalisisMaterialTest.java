package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AnalisisMaterialTest {

  @Test
  void unAnalisisValidoLlevaTipoEstadoYPrecioSugerido() {
    AnalisisMaterial analisis =
        AnalisisMaterial.generar(
            ResultadoAnalisis.VALIDO,
            TipoMaterialSugerido.PET,
            2.5,
            EstadoMaterial.BUENO,
            new PrecioSugerido(new BigDecimal("0.27")),
            "Material en buen estado.",
            "user_123");

    assertNotNull(analisis.id());
    assertEquals(ResultadoAnalisis.VALIDO, analisis.resultado());
    assertEquals(TipoMaterialSugerido.PET, analisis.tipoMaterial());
    assertEquals(2.5, analisis.pesoEstimadoKg());
    assertEquals(EstadoMaterial.BUENO, analisis.estadoMaterial());
    assertEquals(new PrecioSugerido(new BigDecimal("0.27")), analisis.precioSugerido());
    assertEquals("user_123", analisis.solicitanteClerkId());
    assertNotNull(analisis.fechaAnalisis());
  }

  @Test
  void unAnalisisValidoPuedeQuedarseSinPesoSiLaIaNoPudoEstimarlo() {
    AnalisisMaterial analisis =
        AnalisisMaterial.generar(
            ResultadoAnalisis.VALIDO,
            TipoMaterialSugerido.CARTON,
            null,
            EstadoMaterial.EXCELENTE,
            new PrecioSugerido(new BigDecimal("0.10")),
            null,
            "user_123");

    assertNull(analisis.pesoEstimadoKg());
  }

  @Test
  void unAnalisisValidoExigeTipoEstadoYPrecio() {
    assertThrows(
        NullPointerException.class,
        () ->
            AnalisisMaterial.generar(
                ResultadoAnalisis.VALIDO,
                null,
                2.5,
                EstadoMaterial.BUENO,
                new PrecioSugerido(new BigDecimal("0.27")),
                null,
                "user_123"));
    assertThrows(
        NullPointerException.class,
        () ->
            AnalisisMaterial.generar(
                ResultadoAnalisis.VALIDO,
                TipoMaterialSugerido.PET,
                2.5,
                null,
                new PrecioSugerido(new BigDecimal("0.27")),
                null,
                "user_123"));
    assertThrows(
        NullPointerException.class,
        () ->
            AnalisisMaterial.generar(
                ResultadoAnalisis.VALIDO,
                TipoMaterialSugerido.PET,
                2.5,
                EstadoMaterial.BUENO,
                null,
                null,
                "user_123"));
  }

  @Test
  void unAnalisisRechazadoNoPuedeLlevarSugerencias() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AnalisisMaterial.generar(
                ResultadoAnalisis.NO_ES_RECICLAJE,
                TipoMaterialSugerido.PET,
                null,
                null,
                null,
                "La foto muestra un gato.",
                "user_123"));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            AnalisisMaterial.generar(
                ResultadoAnalisis.FOTO_NO_CLARA,
                null,
                3.0,
                null,
                null,
                "Acércate más al material.",
                "user_123"));
  }

  @Test
  void unAnalisisRechazadoSoloConservaLaRecomendacion() {
    AnalisisMaterial analisis =
        AnalisisMaterial.generar(
            ResultadoAnalisis.MULTIPLES_MATERIALES,
            null,
            null,
            null,
            null,
            "Muestra un solo tipo de material.",
            "user_123");

    assertEquals(ResultadoAnalisis.MULTIPLES_MATERIALES, analisis.resultado());
    assertNull(analisis.tipoMaterial());
    assertNull(analisis.pesoEstimadoKg());
    assertNull(analisis.estadoMaterial());
    assertNull(analisis.precioSugerido());
    assertEquals("Muestra un solo tipo de material.", analisis.recomendacion());
  }
}
