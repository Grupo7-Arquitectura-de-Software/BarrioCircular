package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ObtenerPerfilPorClerkIdUseCaseTest {

  @Test
  void obtienePerfilUsandoLaCuentaAsociadaAlClerkId() {
    CuentaAccesoConsultor consultorCuentasAcceso = mock(CuentaAccesoConsultor.class);
    PerfilUsuarioRepository repositorioPerfiles = mock(PerfilUsuarioRepository.class);
    ObtenerPerfilPorClerkIdUseCase casoUso =
        new ObtenerPerfilPorClerkIdUseCase(consultorCuentasAcceso, repositorioPerfiles);
    UUID cuentaUsuarioId = UUID.randomUUID();
    PerfilUsuario perfilUsuario = crearPerfilUsuario(cuentaUsuarioId);
    when(consultorCuentasAcceso.obtenerCuentaIdPorClerkId("user_123"))
        .thenReturn(Optional.of(cuentaUsuarioId));
    when(repositorioPerfiles.buscarPorCuentaUsuarioId(cuentaUsuarioId))
        .thenReturn(Optional.of(perfilUsuario));

    PerfilResultado resultado = casoUso.ejecutar("user_123");

    assertEquals(cuentaUsuarioId, resultado.cuentaUsuarioId());
    assertEquals("CIUDADANO", resultado.rol());
  }

  @Test
  void fallaDeFormaControladaSiNoExisteCuenta() {
    CuentaAccesoConsultor consultorCuentasAcceso = mock(CuentaAccesoConsultor.class);
    PerfilUsuarioRepository repositorioPerfiles = mock(PerfilUsuarioRepository.class);
    ObtenerPerfilPorClerkIdUseCase casoUso =
        new ObtenerPerfilPorClerkIdUseCase(consultorCuentasAcceso, repositorioPerfiles);
    when(consultorCuentasAcceso.obtenerCuentaIdPorClerkId("desconocido"))
        .thenReturn(Optional.empty());

    assertThrows(CuentaAccesoNoEncontradaException.class, () -> casoUso.ejecutar("desconocido"));
  }

  @Test
  void fallaDeFormaControladaSiLaCuentaAunNoTienePerfil() {
    CuentaAccesoConsultor consultorCuentasAcceso = mock(CuentaAccesoConsultor.class);
    PerfilUsuarioRepository repositorioPerfiles = mock(PerfilUsuarioRepository.class);
    ObtenerPerfilPorClerkIdUseCase casoUso =
        new ObtenerPerfilPorClerkIdUseCase(consultorCuentasAcceso, repositorioPerfiles);
    UUID cuentaUsuarioId = UUID.randomUUID();
    when(consultorCuentasAcceso.obtenerCuentaIdPorClerkId("user_123"))
        .thenReturn(Optional.of(cuentaUsuarioId));
    when(repositorioPerfiles.buscarPorCuentaUsuarioId(cuentaUsuarioId))
        .thenReturn(Optional.empty());

    assertThrows(PerfilNoEncontradoException.class, () -> casoUso.ejecutar("user_123"));
  }

  private PerfilUsuario crearPerfilUsuario(UUID cuentaUsuarioId) {
    return PerfilUsuario.reconstituir(
        UUID.randomUUID(),
        cuentaUsuarioId,
        new DocumentoIdentificacion("1712345678"),
        "Ana Perez",
        null,
        RolUsuario.CIUDADANO,
        EstadoPerfil.ACTIVO,
        new InformacionContacto("ana@correo.com", "0999999999"),
        new CoordenadaGPS(-0.1807, -78.4678),
        LocalDateTime.now());
  }
}
