package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class SugerenciaPrecioTest {

  @Test
  void generarSellaFechaYConservaLosDatos() {
    PrecioSugerido precio = PrecioSugerido.de(0.30);

    SugerenciaPrecio sugerencia =
        SugerenciaPrecio.generar(
            TipoMaterialSugerido.PET,
            15.0,
            precio,
            FuenteSugerencia.IA_GROQ,
            "justificacion",
            "user_123");

    assertNotNull(sugerencia.id());
    assertEquals(TipoMaterialSugerido.PET, sugerencia.tipoMaterial());
    assertEquals(15.0, sugerencia.pesoKg());
    assertEquals(precio, sugerencia.precioSugerido());
    assertEquals(FuenteSugerencia.IA_GROQ, sugerencia.fuente());
    assertEquals("justificacion", sugerencia.justificacion());
    assertEquals("user_123", sugerencia.solicitanteClerkId());
    assertNotNull(sugerencia.fechaSugerencia());
  }

  @Test
  void reconstituirPreservaLaIdentidadOriginal() {
    SugerenciaPrecioId id = SugerenciaPrecioId.nuevo();
    Instant fecha = Instant.now();

    SugerenciaPrecio sugerencia =
        SugerenciaPrecio.reconstituir(
            id,
            TipoMaterialSugerido.CARTON,
            null,
            PrecioSugerido.de(0.10),
            FuenteSugerencia.CATALOGO_RESPALDO,
            null,
            null,
            fecha);

    assertEquals(id, sugerencia.id());
    assertEquals(fecha, sugerencia.fechaSugerencia());
  }
}
