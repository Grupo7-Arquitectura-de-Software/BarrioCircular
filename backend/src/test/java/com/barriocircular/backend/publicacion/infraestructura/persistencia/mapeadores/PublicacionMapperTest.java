package com.barriocircular.backend.publicacion.infraestructura.persistencia.mapeadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa.PublicacionEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PublicacionMapperTest {

  private final PublicacionMapper mapper = new PublicacionMapper();

  @Test
  void conservaPesoRealYObservacionesAlMapearDesdeYHaciaEntidad() {
    UUID id = UUID.randomUUID();
    UUID creadorId = UUID.randomUUID();
    UUID reservadoPor = UUID.randomUUID();
    PublicacionEntity entity =
        new PublicacionEntity(
            id,
            creadorId,
            "PET",
            12.5,
            BigDecimal.valueOf(0.30),
            -0.2,
            -78.5,
            "https://example.com/foto.jpg",
            Instant.parse("2026-07-09T14:00:00Z"),
            "FINALIZADA",
            reservadoPor,
            11.4,
            "Material humedo.");

    Publicacion dominio = mapper.toDomain(entity);
    PublicacionEntity remapeada = mapper.toEntity(dominio);

    assertEquals(EstadoPublicacion.FINALIZADA, dominio.estado());
    assertEquals(11.4, dominio.pesoRealVerificado());
    assertEquals("Material humedo.", dominio.observacionesVerificacion());
    assertEquals(11.4, remapeada.getPesoRealVerificado());
    assertEquals("Material humedo.", remapeada.getObservacionesVerificacion());
  }

  @Test
  void permitePublicacionesAntiguasSinVerificacion() {
    PublicacionEntity entity =
        new PublicacionEntity(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "PET",
            12.5,
            BigDecimal.valueOf(0.30),
            -0.2,
            -78.5,
            "https://example.com/foto.jpg",
            Instant.parse("2026-07-09T14:00:00Z"),
            "RESERVADA",
            UUID.randomUUID(),
            null,
            null);

    Publicacion dominio = mapper.toDomain(entity);

    assertNull(dominio.pesoRealVerificado());
    assertNull(dominio.observacionesVerificacion());
  }
}
