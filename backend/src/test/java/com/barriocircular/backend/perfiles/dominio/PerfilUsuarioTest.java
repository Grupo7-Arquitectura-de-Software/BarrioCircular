package com.barriocircular.backend.perfiles.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.perfiles.dominio.eventos.PerfilCreado;
import com.barriocircular.backend.perfiles.dominio.excepciones.DocumentoIdentificacionInvalidoException;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilSuspendidoException;
import com.barriocircular.backend.perfiles.dominio.excepciones.RolInvalidoException;
import com.barriocircular.backend.perfiles.dominio.excepciones.UbicacionFueraDeQuitoException;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PerfilUsuarioTest {

  @Test
  void crearPerfilSinRolDebeLanzarExcepcionDominio() {
    assertThrows(
        RolInvalidoException.class,
        () ->
            PerfilUsuarioFactory.crearPerfil(
                UUID.randomUUID(),
                documentoValido(),
                "Ana Perez",
                null,
                null,
                contactoValido(),
                ubicacionValida()));
  }

  @Test
  void actualizarUbicacionHabitualDentroDeQuitoDebeSerExitosa() {
    PerfilUsuario perfil = crearCiudadano();
    CoordenadaGPS nuevaUbicacion = new CoordenadaGPS(-0.18, -78.48);

    perfil.actualizarUbicacionHabitual(nuevaUbicacion);

    assertEquals(nuevaUbicacion, perfil.getUbicacionHabitual());
  }

  @Test
  void coordenadaFueraDeQuitoDebeLanzarExcepcion() {
    assertThrows(UbicacionFueraDeQuitoException.class, () -> new CoordenadaGPS(-2.17, -79.92));
  }

  @Test
  void perfilSuspendidoNoDebeActualizarInformacionContacto() {
    PerfilUsuario perfil = crearCiudadano();
    perfil.suspender();

    assertThrows(
        PerfilSuspendidoException.class,
        () ->
            perfil.actualizarInformacionContacto(
                new InformacionContacto("nuevo@correo.com", "0987654321")));
  }

  @Test
  void perfilSuspendidoNoDebeCambiarRol() {
    PerfilUsuario perfil = crearCiudadano();
    perfil.suspender();

    assertThrows(PerfilSuspendidoException.class, () -> perfil.cambiarRol(RolUsuario.RECICLADOR));
  }

  @Test
  void crearPerfilConDocumentoInvalidoDebeLanzarExcepcion() {
    assertThrows(
        DocumentoIdentificacionInvalidoException.class,
        () -> new DocumentoIdentificacion("ABC123"));
  }

  @Test
  void crearPerfilCentroRecoleccionSinNombreComercialDebeLanzarExcepcion() {
    PerfilDomainException excepcion =
        assertThrows(
            PerfilDomainException.class,
            () ->
                PerfilUsuarioFactory.crearPerfil(
                    UUID.randomUUID(),
                    documentoValido(),
                    null,
                    null,
                    RolUsuario.CENTRO_RECOLECCION,
                    contactoValido(),
                    ubicacionValida()));

    assertTrue(excepcion.getMessage().contains("nombre comercial"));
  }

  @Test
  void ciudadanoActivoPuedePublicarMateriales() {
    assertTrue(crearCiudadano().puedePublicarMateriales());
  }

  @Test
  void centroRecoleccionActivoPuedeComprarMateriales() {
    PerfilUsuario centro =
        PerfilUsuarioFactory.crearPerfil(
            UUID.randomUUID(),
            documentoValido(),
            null,
            "Centro Norte",
            RolUsuario.CENTRO_RECOLECCION,
            contactoValido(),
            ubicacionValida());

    assertTrue(centro.puedeComprarMateriales());
  }

  @Test
  void perfilSuspendidoNoPuedePublicarNiComprar() {
    PerfilUsuario perfil =
        PerfilUsuarioFactory.crearPerfil(
            UUID.randomUUID(),
            documentoValido(),
            "Carlos Lopez",
            null,
            RolUsuario.RECICLADOR,
            contactoValido(),
            ubicacionValida());
    perfil.suspender();

    assertFalse(perfil.puedePublicarMateriales());
    assertFalse(perfil.puedeComprarMateriales());
  }

  @Test
  void perfilCreadoDebeRegistrarEventoDeDominio() {
    PerfilUsuario perfil = crearCiudadano();

    assertTrue(perfil.obtenerEventosDominio().stream().anyMatch(PerfilCreado.class::isInstance));
  }

  private PerfilUsuario crearCiudadano() {
    return PerfilUsuarioFactory.crearPerfil(
        UUID.randomUUID(),
        documentoValido(),
        "Ana Perez",
        null,
        RolUsuario.CIUDADANO,
        contactoValido(),
        ubicacionValida());
  }

  private DocumentoIdentificacion documentoValido() {
    return new DocumentoIdentificacion("1712345678");
  }

  private InformacionContacto contactoValido() {
    return new InformacionContacto("ana@correo.com", "0999999999");
  }

  private CoordenadaGPS ubicacionValida() {
    return new CoordenadaGPS(-0.20, -78.50);
  }
}
