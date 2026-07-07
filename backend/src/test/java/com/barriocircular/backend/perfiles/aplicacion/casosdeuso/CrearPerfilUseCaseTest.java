package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilYaExisteException;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;
import com.barriocircular.backend.perfiles.dominio.eventos.PerfilCreado;
import com.barriocircular.backend.perfiles.dominio.excepciones.DocumentoIdentificacionInvalidoException;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class CrearPerfilUseCaseTest {

  @Test
  void crearPerfilGuardaPublicaEventosYLosLimpia() {
    PerfilUsuarioRepositoryFake repositorioPerfiles = new PerfilUsuarioRepositoryFake();
    PerfilOnboardingPendienteRepositoryFake repositorioOnboarding =
        new PerfilOnboardingPendienteRepositoryFake();
    EventosPublicados eventosPublicados = new EventosPublicados();
    CuentaAccesoConsultorFake cuentaAccesoConsultor = new CuentaAccesoConsultorFake();
    CrearPerfilUseCase casoUso =
        new CrearPerfilUseCase(
            repositorioPerfiles, repositorioOnboarding, eventosPublicados, cuentaAccesoConsultor);
    UUID cuentaUsuarioId = UUID.randomUUID();
    repositorioOnboarding.cuentasPendientes.add(cuentaUsuarioId);
    String clerkId = "user_2test123";
    cuentaAccesoConsultor.agregarCuenta(clerkId, cuentaUsuarioId);

    PerfilResultado resultado =
        casoUso.ejecutar(
            new CrearPerfilCommand(
                "1712345678",
                "Ana Perez",
                null,
                "CIUDADANO",
                "ana@correo.com",
                "0999999999",
                -0.1807,
                -78.4678),
            clerkId);

    assertEquals(resultado.perfilId(), repositorioPerfiles.perfilGuardado.getId());
    assertEquals("CIUDADANO", resultado.rol());
    assertEquals("ACTIVO", resultado.estadoPerfil());
    assertEquals("1712345678", resultado.documentoIdentificacion());
    assertEquals("ana@correo.com", resultado.correoElectronico());
    assertTrue(eventosPublicados.eventos.stream().anyMatch(PerfilCreado.class::isInstance));
    assertTrue(repositorioPerfiles.perfilGuardado.obtenerEventosDominio().isEmpty());
    assertFalse(repositorioOnboarding.cuentasPendientes.contains(cuentaUsuarioId));
  }

  @Test
  void crearPerfilConDocumentoInvalidoDebeLanzarExcepcionYNoGuardar() {
    PerfilUsuarioRepositoryFake repositorioPerfiles = new PerfilUsuarioRepositoryFake();
    PerfilOnboardingPendienteRepositoryFake repositorioOnboarding =
        new PerfilOnboardingPendienteRepositoryFake();
    EventosPublicados eventosPublicados = new EventosPublicados();
    CuentaAccesoConsultorFake cuentaAccesoConsultor = new CuentaAccesoConsultorFake();
    CrearPerfilUseCase casoUso =
        new CrearPerfilUseCase(
            repositorioPerfiles, repositorioOnboarding, eventosPublicados, cuentaAccesoConsultor);

    UUID cuentaUsuarioId = UUID.randomUUID();
    String clerkId = "user_2test123";
    cuentaAccesoConsultor.agregarCuenta(clerkId, cuentaUsuarioId);

    assertThrows(
        DocumentoIdentificacionInvalidoException.class,
        () ->
            casoUso.ejecutar(
                new CrearPerfilCommand(
                    "ABC123",
                    "Ana Perez",
                    null,
                    "CIUDADANO",
                    "ana@correo.com",
                    "0999999999",
                    -0.1807,
                    -78.4678),
                clerkId));

    assertEquals(0, repositorioPerfiles.cantidadGuardados);
    assertTrue(eventosPublicados.eventos.isEmpty());
  }

  @Test
  void noPermiteCrearDosPerfilesParaLaMismaCuenta() {
    PerfilUsuarioRepositoryFake repositorioPerfiles = new PerfilUsuarioRepositoryFake();
    PerfilOnboardingPendienteRepositoryFake repositorioOnboarding =
        new PerfilOnboardingPendienteRepositoryFake();
    CuentaAccesoConsultorFake cuentaAccesoConsultor = new CuentaAccesoConsultorFake();
    CrearPerfilUseCase casoUso =
        new CrearPerfilUseCase(
            repositorioPerfiles,
            repositorioOnboarding,
            new EventosPublicados(),
            cuentaAccesoConsultor);
    UUID cuentaUsuarioId = UUID.randomUUID();
    String clerkId = "user_2test123";
    cuentaAccesoConsultor.agregarCuenta(clerkId, cuentaUsuarioId);
    CrearPerfilCommand comando =
        new CrearPerfilCommand(
            "1712345678",
            "Ana Perez",
            null,
            "CIUDADANO",
            "ana@correo.com",
            "0999999999",
            -0.1807,
            -78.4678);

    casoUso.ejecutar(comando, clerkId);

    assertThrows(PerfilYaExisteException.class, () -> casoUso.ejecutar(comando, clerkId));
    assertEquals(1, repositorioPerfiles.cantidadGuardados);
  }

  @Test
  void crearPerfilConClerkIdNoEncontradoDebeLanzarExcepcion() {
    PerfilUsuarioRepositoryFake repositorioPerfiles = new PerfilUsuarioRepositoryFake();
    PerfilOnboardingPendienteRepositoryFake repositorioOnboarding =
        new PerfilOnboardingPendienteRepositoryFake();
    CuentaAccesoConsultorFake cuentaAccesoConsultor = new CuentaAccesoConsultorFake();
    CrearPerfilUseCase casoUso =
        new CrearPerfilUseCase(
            repositorioPerfiles,
            repositorioOnboarding,
            new EventosPublicados(),
            cuentaAccesoConsultor);

    CrearPerfilCommand comando =
        new CrearPerfilCommand(
            "1712345678",
            "Ana Perez",
            null,
            "CIUDADANO",
            "ana@correo.com",
            "0999999999",
            -0.1807,
            -78.4678);

    assertThrows(
        CuentaAccesoNoEncontradaException.class,
        () -> casoUso.ejecutar(comando, "clerk_id_no_existe"));
    assertEquals(0, repositorioPerfiles.cantidadGuardados);
  }

  private static final class EventosPublicados implements ApplicationEventPublisher {

    private final List<Object> eventos = new ArrayList<>();

    @Override
    public void publishEvent(Object evento) {
      eventos.add(evento);
    }
  }

  private static final class PerfilUsuarioRepositoryFake implements PerfilUsuarioRepository {

    private PerfilUsuario perfilGuardado;
    private int cantidadGuardados;

    @Override
    public void guardar(PerfilUsuario perfil) {
      this.perfilGuardado = perfil;
      cantidadGuardados++;
    }

    @Override
    public Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId) {
      return Optional.ofNullable(perfilGuardado)
          .filter(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId));
    }

    @Override
    public boolean existePorDocumentoIdentificacion(
        DocumentoIdentificacion documentoIdentificacion) {
      return perfilGuardado != null
          && perfilGuardado.getDocumentoIdentificacion().equals(documentoIdentificacion);
    }

    @Override
    public boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId) {
      return perfilGuardado != null && perfilGuardado.getCuentaUsuarioId().equals(cuentaUsuarioId);
    }
  }

  private static final class PerfilOnboardingPendienteRepositoryFake
      implements PerfilOnboardingPendienteRepository {

    private final Set<UUID> cuentasPendientes = new HashSet<>();

    @Override
    public void guardar(PerfilOnboardingPendiente onboardingPendiente) {
      cuentasPendientes.add(onboardingPendiente.cuentaId());
    }

    @Override
    public boolean existePorCuentaId(UUID cuentaId) {
      return cuentasPendientes.contains(cuentaId);
    }

    @Override
    public void eliminarPorCuentaId(UUID cuentaId) {
      cuentasPendientes.remove(cuentaId);
    }
  }

  public static final class CuentaAccesoConsultorFake implements CuentaAccesoConsultor {

    private final Map<String, UUID> cuentas = new HashMap<>();

    public void agregarCuenta(String clerkId, UUID cuentaId) {
      cuentas.put(clerkId, cuentaId);
    }

    @Override
    public Optional<UUID> obtenerCuentaIdPorClerkId(String clerkId) {
      return Optional.ofNullable(cuentas.get(clerkId));
    }
  }
}
