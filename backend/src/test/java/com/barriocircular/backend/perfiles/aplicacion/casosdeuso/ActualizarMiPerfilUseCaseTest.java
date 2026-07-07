package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.barriocircular.backend.perfiles.aplicacion.comandos.ActualizarMiPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilSuspendidoException;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class ActualizarMiPerfilUseCaseTest {

  @Test
  void actualizaPerfilAutenticadoCorrectamente() {
    UUID cuentaUsuarioId = UUID.randomUUID();
    PerfilUsuario perfil = crearPerfil(cuentaUsuarioId);
    PerfilUsuarioRepositoryFake repositorio = new PerfilUsuarioRepositoryFake();
    repositorio.guardar(perfil);
    ActualizarMiPerfilUseCase useCase =
        new ActualizarMiPerfilUseCase(
            new CuentaAccesoConsultorFake(cuentaUsuarioId), repositorio, new EventosPublicados());

    PerfilResultado resultado =
        useCase.ejecutar(
            new ActualizarMiPerfilCommand(
                "user_123", "Ana", "Gomez", "0987654321", "Av. Amazonas", -0.18, -78.48));

    assertEquals(perfil.getId(), resultado.perfilId());
    assertEquals(cuentaUsuarioId, resultado.cuentaUsuarioId());
    assertEquals("Ana Gomez", resultado.nombreCompleto());
    assertEquals("0987654321", resultado.telefono());
    assertEquals("Av. Amazonas", resultado.direccionHabitual());
    assertEquals(-0.18, resultado.latitud());
    assertEquals(-78.48, resultado.longitud());
  }

  @Test
  void noPermiteActualizarPerfilInexistente() {
    UUID cuentaUsuarioId = UUID.randomUUID();
    ActualizarMiPerfilUseCase useCase =
        new ActualizarMiPerfilUseCase(
            new CuentaAccesoConsultorFake(cuentaUsuarioId),
            new PerfilUsuarioRepositoryFake(),
            new EventosPublicados());

    assertThrows(
        PerfilNoEncontradoException.class,
        () ->
            useCase.ejecutar(
                new ActualizarMiPerfilCommand(
                    "user_123", "Ana", "Gomez", "0987654321", null, null, null)));
  }

  @Test
  void noPermiteActualizarPerfilSuspendido() {
    UUID cuentaUsuarioId = UUID.randomUUID();
    PerfilUsuario perfil = crearPerfil(cuentaUsuarioId);
    perfil.suspender();
    PerfilUsuarioRepositoryFake repositorio = new PerfilUsuarioRepositoryFake();
    repositorio.guardar(perfil);
    ActualizarMiPerfilUseCase useCase =
        new ActualizarMiPerfilUseCase(
            new CuentaAccesoConsultorFake(cuentaUsuarioId), repositorio, new EventosPublicados());

    assertThrows(
        PerfilSuspendidoException.class,
        () ->
            useCase.ejecutar(
                new ActualizarMiPerfilCommand(
                    "user_123", null, null, "0987654321", null, null, null)));
  }

  private PerfilUsuario crearPerfil(UUID cuentaUsuarioId) {
    return PerfilUsuarioFactory.crearPerfil(
        cuentaUsuarioId,
        new DocumentoIdentificacion("1712345678"),
        "Ana Perez",
        null,
        RolUsuario.CIUDADANO,
        new InformacionContacto("ana@correo.com", "0999999999"),
        new CoordenadaGPS(-0.1807, -78.4678));
  }

  private static final class CuentaAccesoConsultorFake implements CuentaAccesoConsultor {

    private final UUID cuentaUsuarioId;

    private CuentaAccesoConsultorFake(UUID cuentaUsuarioId) {
      this.cuentaUsuarioId = cuentaUsuarioId;
    }

    @Override
    public Optional<UUID> obtenerCuentaIdPorClerkId(String clerkId) {
      return Optional.of(cuentaUsuarioId);
    }
  }

  private static final class EventosPublicados implements ApplicationEventPublisher {

    @Override
    public void publishEvent(Object event) {}
  }

  private static final class PerfilUsuarioRepositoryFake implements PerfilUsuarioRepository {

    private final Map<UUID, PerfilUsuario> perfiles = new HashMap<>();

    @Override
    public void guardar(PerfilUsuario perfil) {
      perfiles.put(perfil.getId(), perfil);
    }

    @Override
    public Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId) {
      return perfiles.values().stream()
          .filter(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId))
          .findFirst();
    }

    @Override
    public boolean existePorDocumentoIdentificacion(
        DocumentoIdentificacion documentoIdentificacion) {
      return perfiles.values().stream()
          .anyMatch(perfil -> perfil.getDocumentoIdentificacion().equals(documentoIdentificacion));
    }

    @Override
    public boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId) {
      return perfiles.values().stream()
          .anyMatch(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId));
    }
  }
}
