package com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso;

import static org.assertj.core.api.Assertions.assertThat;

import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilElegible;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilVerificable;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.EstadoCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.RolCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.repositorios.CredencialVerificacionRepositorio;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VerificarCredencialUseCaseTest {

  private static final String TOKEN_VALIDO = "token-publico-de-prueba-000000000000000";

  @Test
  void tokenInexistenteDevuelveInvalido() {
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(new RepositorioEnMemoria(), perfilConsultor(true, true));

    var resultado = useCase.ejecutar(TOKEN_VALIDO);

    assertThat(resultado.valido()).isFalse();
    assertThat(resultado.nombreMostrado()).isNull();
  }

  @Test
  void credencialRevocadaDevuelveInvalido() {
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    CredencialVerificacion credencial = credencialActiva(UUID.randomUUID(), TOKEN_VALIDO);
    credencial.revocar();
    repositorio.guardar(credencial);
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(repositorio, perfilConsultor(true, true));

    assertThat(useCase.ejecutar(TOKEN_VALIDO).valido()).isFalse();
  }

  @Test
  void credencialExpiradaDevuelveInvalido() {
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    repositorio.guardar(
        CredencialVerificacion.reconstituir(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RolCredencial.RECICLADOR,
            new TokenVerificacion(TOKEN_VALIDO),
            EstadoCredencial.ACTIVA,
            Instant.now().minus(10, ChronoUnit.DAYS),
            Instant.now().minus(1, ChronoUnit.DAYS),
            null));
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(repositorio, perfilConsultor(true, true));

    assertThat(useCase.ejecutar(TOKEN_VALIDO).valido()).isFalse();
  }

  @Test
  void cuentaNoActivaDevuelveInvalido() {
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    repositorio.guardar(credencialActiva(UUID.randomUUID(), TOKEN_VALIDO));
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(repositorio, perfilConsultor(true, false));

    assertThat(useCase.ejecutar(TOKEN_VALIDO).valido()).isFalse();
  }

  @Test
  void perfilNoActivoDevuelveInvalido() {
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    repositorio.guardar(credencialActiva(UUID.randomUUID(), TOKEN_VALIDO));
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(repositorio, perfilConsultor(false, true));

    assertThat(useCase.ejecutar(TOKEN_VALIDO).valido()).isFalse();
  }

  @Test
  void credencialValidaRegistraVerificacionYDevuelveDatosPublicos() {
    UUID perfilId = UUID.randomUUID();
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    CredencialVerificacion credencial = credencialActiva(perfilId, TOKEN_VALIDO);
    repositorio.guardar(credencial);
    VerificarCredencialUseCase useCase =
        new VerificarCredencialUseCase(repositorio, perfilConsultor(true, true));

    var resultado = useCase.ejecutar(TOKEN_VALIDO);

    assertThat(resultado.valido()).isTrue();
    assertThat(resultado.nombreMostrado()).isEqualTo("Ana Perez");
    assertThat(resultado.rol()).isEqualTo("RECICLADOR");
    assertThat(credencial.getFechaUltimaVerificacion()).isNotNull();
  }

  private static CredencialVerificacion credencialActiva(UUID perfilId, String token) {
    return CredencialVerificacion.reconstituir(
        UUID.randomUUID(),
        perfilId,
        RolCredencial.RECICLADOR,
        new TokenVerificacion(token),
        EstadoCredencial.ACTIVA,
        Instant.now().minus(2, ChronoUnit.DAYS),
        Instant.now().plus(30, ChronoUnit.DAYS),
        null);
  }

  private static PerfilConsultor perfilConsultor(boolean perfilActivo, boolean cuentaActiva) {
    return new PerfilConsultor() {
      @Override
      public Optional<PerfilElegible> obtenerPorClerkId(String clerkId) {
        return Optional.empty();
      }

      @Override
      public Optional<PerfilVerificable> obtenerPorPerfilId(UUID perfilId) {
        return Optional.of(
            new PerfilVerificable(
                perfilId,
                "Ana Perez",
                "RECICLADOR",
                perfilActivo,
                cuentaActiva,
                Instant.now().minus(40, ChronoUnit.DAYS)));
      }
    };
  }

  private static class RepositorioEnMemoria implements CredencialVerificacionRepositorio {
    private final List<CredencialVerificacion> credenciales = new ArrayList<>();

    @Override
    public CredencialVerificacion guardar(CredencialVerificacion credencial) {
      credenciales.removeIf(existente -> existente.getId().equals(credencial.getId()));
      credenciales.add(credencial);
      return credencial;
    }

    @Override
    public Optional<CredencialVerificacion> buscarPorToken(TokenVerificacion token) {
      return credenciales.stream().filter(c -> c.getToken().equals(token)).findFirst();
    }

    @Override
    public Optional<CredencialVerificacion> buscarActivaPorPerfil(UUID perfilId) {
      return credenciales.stream()
          .filter(c -> c.getPerfilId().equals(perfilId))
          .filter(c -> c.getEstado() == EstadoCredencial.ACTIVA)
          .findFirst();
    }

    @Override
    public List<CredencialVerificacion> listarPorPerfil(UUID perfilId) {
      return credenciales.stream().filter(c -> c.getPerfilId().equals(perfilId)).toList();
    }
  }
}
