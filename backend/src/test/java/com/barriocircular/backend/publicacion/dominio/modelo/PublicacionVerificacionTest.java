package com.barriocircular.backend.publicacion.dominio.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;
import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PublicacionVerificacionTest {

  @Test
  void finalizaConPesoRealYObservacionesNormalizadasSinModificarPesoPublicado() {
    Publicacion publicacion = publicacionReservada();

    publicacion.finalizarConVerificacion(8.7, "  Material humedo.  ");

    assertEquals(EstadoPublicacion.FINALIZADA, publicacion.estado());
    assertEquals(8.7, publicacion.pesoRealVerificado());
    assertEquals("Material humedo.", publicacion.observacionesVerificacion());
    assertEquals(12.5, publicacion.detalle().peso().valorKg());
  }

  @Test
  void observacionesVaciasSeGuardanComoNull() {
    Publicacion publicacion = publicacionReservada();

    publicacion.finalizarConVerificacion(8.7, "   ");

    assertNull(publicacion.observacionesVerificacion());
  }

  @Test
  void rechazaPesoRealNoFinitoOCero() {
    Publicacion publicacion = publicacionReservada();

    assertThrows(
        PublicacionInvalidaException.class,
        () -> publicacion.finalizarConVerificacion(Double.NaN, null));
    assertThrows(
        PublicacionInvalidaException.class, () -> publicacion.finalizarConVerificacion(0, null));
  }

  @Test
  void rechazaConfirmarDosVeces() {
    Publicacion publicacion = publicacionReservada();
    publicacion.finalizarConVerificacion(8.7, null);

    assertThrows(
        EstadoInvalidoException.class, () -> publicacion.finalizarConVerificacion(8.7, null));
  }

  private Publicacion publicacionReservada() {
    Publicacion publicacion =
        Publicacion.crear(
            PublicacionId.de(UUID.randomUUID()),
            CiudadanoId.de(UUID.randomUUID()),
            new DetalleMaterial(TipoResiduo.CARTON, PesoEstimado.deKilos(12.5)),
            new PrecioPorKilo(BigDecimal.valueOf(0.10)),
            new UbicacionRecogida(-0.2, -78.5),
            new EvidenciaVisual("https://example.com/foto.jpg"));
    publicacion.reservarPublicacionPor(ReservadorId.de(UUID.randomUUID()));
    return publicacion;
  }
}
