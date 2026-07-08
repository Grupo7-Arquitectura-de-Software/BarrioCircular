package com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.PerfilNoActivoException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.GeneradorTokenPort;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilElegible;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilVerificable;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.UrlVerificacionBuilder;
import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.RolNoElegibleException;
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

class EmitirCredencialUseCaseTest {

  @Test
  void recicladorPuedeEmitirCredencial() {
    UUID perfilId = UUID.randomUUID();
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    EmitirCredencialUseCase useCase =
        useCase(
            repositorio,
            perfilConsultor(
                new PerfilElegible(perfilId, "Ana", "RECICLADOR", true, true, Instant.now())));

    var resultado = useCase.ejecutar("user_reciclador");

    assertThat(resultado.credencialId()).isNotNull();
    assertThat(resultado.urlVerificacion()).startsWith("https://barriocircular.site/verificar/");
    assertThat(repositorio.credenciales).hasSize(1);
  }

  @Test
  void centroRecoleccionPuedeEmitirCredencial() {
    UUID perfilId = UUID.randomUUID();
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    EmitirCredencialUseCase useCase =
        useCase(
            repositorio,
            perfilConsultor(
                new PerfilElegible(
                    perfilId, "Centro Norte", "CENTRO_RECOLECCION", true, true, Instant.now())));

    useCase.ejecutar("user_centro");

    assertThat(repositorio.credenciales).hasSize(1);
  }

  @Test
  void ciudadanoNoPuedeEmitirCredencial() {
    EmitirCredencialUseCase useCase =
        useCase(
            new RepositorioEnMemoria(),
            perfilConsultor(
                new PerfilElegible(
                    UUID.randomUUID(), "Luis", "CIUDADANO", true, true, Instant.now())));

    assertThatThrownBy(() -> useCase.ejecutar("user_ciudadano"))
        .isInstanceOf(RolNoElegibleException.class);
  }

  @Test
  void perfilNoActivoNoPuedeEmitirCredencial() {
    EmitirCredencialUseCase useCase =
        useCase(
            new RepositorioEnMemoria(),
            perfilConsultor(
                new PerfilElegible(
                    UUID.randomUUID(), "Ana", "RECICLADOR", false, true, Instant.now())));

    assertThatThrownBy(() -> useCase.ejecutar("user_suspendido"))
        .isInstanceOf(PerfilNoActivoException.class);
  }

  @Test
  void emitirEsIdempotenteSiYaExisteCredencialActiva() {
    UUID perfilId = UUID.randomUUID();
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    EmitirCredencialUseCase useCase =
        useCase(
            repositorio,
            perfilConsultor(
                new PerfilElegible(perfilId, "Ana", "RECICLADOR", true, true, Instant.now())));

    var primera = useCase.ejecutar("user_reciclador");
    var segunda = useCase.ejecutar("user_reciclador");

    assertThat(segunda.credencialId()).isEqualTo(primera.credencialId());
    assertThat(repositorio.credenciales).hasSize(1);
  }

  @Test
  void emitirNoReutilizaCredencialActivaExpirada() {
    UUID perfilId = UUID.randomUUID();
    RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
    CredencialVerificacion credencialExpirada =
        CredencialVerificacion.reconstituir(
            UUID.randomUUID(),
            perfilId,
            RolCredencial.RECICLADOR,
            new TokenVerificacion("token-expirado-de-prueba-000000000000"),
            EstadoCredencial.ACTIVA,
            Instant.now().minus(400, ChronoUnit.DAYS),
            Instant.now().minus(1, ChronoUnit.DAYS),
            null);
    repositorio.guardar(credencialExpirada);
    EmitirCredencialUseCase useCase =
        useCase(
            repositorio,
            perfilConsultor(
                new PerfilElegible(perfilId, "Ana", "RECICLADOR", true, true, Instant.now())));

    var resultado = useCase.ejecutar("user_reciclador");

    assertThat(resultado.credencialId()).isNotEqualTo(credencialExpirada.getId());
    assertThat(resultado.fechaExpiracion()).isAfter(Instant.now());
    assertThat(repositorio.credenciales).hasSize(2);
  }

  private EmitirCredencialUseCase useCase(
      RepositorioEnMemoria repositorio, PerfilConsultor perfilConsultor) {
    return new EmitirCredencialUseCase(
        perfilConsultor, repositorio, new GeneradorDeterministico(), new UrlBuilderFake());
  }

  private PerfilConsultor perfilConsultor(PerfilElegible perfil) {
    return new PerfilConsultor() {
      @Override
      public Optional<PerfilElegible> obtenerPorClerkId(String clerkId) {
        return Optional.of(perfil);
      }

      @Override
      public Optional<PerfilVerificable> obtenerPorPerfilId(UUID perfilId) {
        return Optional.empty();
      }
    };
  }

  private static class GeneradorDeterministico implements GeneradorTokenPort {
    private int secuencia = 1;

    @Override
    public TokenVerificacion generar() {
      return new TokenVerificacion("token-seguro-de-prueba-000000000000" + secuencia++);
    }
  }

  private static class UrlBuilderFake implements UrlVerificacionBuilder {

    @Override
    public String construir(TokenVerificacion token) {
      return "https://barriocircular.site/verificar/" + token.valor();
    }
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
