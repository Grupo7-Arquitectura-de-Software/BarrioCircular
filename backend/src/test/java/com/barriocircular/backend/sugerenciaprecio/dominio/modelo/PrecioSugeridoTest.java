package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.PrecioSugeridoInvalidoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PrecioSugeridoTest {

  @Test
  void aceptaValoresDentroDelRangoDeCordura() {
    assertDoesNotThrow(() -> new PrecioSugerido(BigDecimal.valueOf(0.01)));
    assertDoesNotThrow(() -> new PrecioSugerido(BigDecimal.valueOf(5.5)));
    assertDoesNotThrow(() -> new PrecioSugerido(new BigDecimal("10.00")));
  }

  @Test
  void rechazaValorCeroONegativo() {
    assertThrows(PrecioSugeridoInvalidoException.class, () -> new PrecioSugerido(BigDecimal.ZERO));
    assertThrows(
        PrecioSugeridoInvalidoException.class, () -> new PrecioSugerido(BigDecimal.valueOf(-1.0)));
  }

  @Test
  void rechazaValorNulo() {
    assertThrows(PrecioSugeridoInvalidoException.class, () -> new PrecioSugerido(null));
  }

  @Test
  void rechazaValorMayorAlTechoDeCordura() {
    assertThrows(
        PrecioSugeridoInvalidoException.class, () -> new PrecioSugerido(new BigDecimal("10.01")));
  }
}
