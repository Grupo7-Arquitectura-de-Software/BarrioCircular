package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EstadoMaterialTest {

  @Test
  void cadaEstadoTieneSuFactorDeAjusteAcotado() {
    assertEquals(new BigDecimal("1.0"), EstadoMaterial.EXCELENTE.factor());
    assertEquals(new BigDecimal("0.9"), EstadoMaterial.BUENO.factor());
    assertEquals(new BigDecimal("0.8"), EstadoMaterial.REGULAR.factor());
  }

  @Test
  void desdeReconoceLosEstadosDelCatalogo() {
    assertEquals(Optional.of(EstadoMaterial.EXCELENTE), EstadoMaterial.desde("EXCELENTE"));
    assertEquals(Optional.of(EstadoMaterial.BUENO), EstadoMaterial.desde("BUENO"));
    assertEquals(Optional.of(EstadoMaterial.REGULAR), EstadoMaterial.desde("REGULAR"));
  }

  @Test
  void desdeDevuelveVacioParaValoresNoReconocidosSinLanzarExcepcion() {
    assertTrue(EstadoMaterial.desde("COMO_NUEVO").isEmpty());
    assertTrue(EstadoMaterial.desde(null).isEmpty());
    assertTrue(EstadoMaterial.desde("").isEmpty());
  }
}
