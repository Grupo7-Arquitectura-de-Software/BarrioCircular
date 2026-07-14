package com.barriocircular.backend.perfiles.infraestructura.mensajeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.acceso.dominio.eventos.UsuarioRegistrado;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.RegistrarOnboardingPerfilPendienteUseCase;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilOnboardingPendiente;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccesoEventConsumerTest {

  @Test
  void usuarioRegistradoRegistraOnboardingPendiente() {
    PerfilOnboardingPendienteRepositoryFake repository =
        new PerfilOnboardingPendienteRepositoryFake();
    RegistrarOnboardingPerfilPendienteUseCase useCase =
        new RegistrarOnboardingPerfilPendienteUseCase(repository);
    AccesoEventConsumer consumer = new AccesoEventConsumer(useCase);
    UUID cuentaId = UUID.randomUUID();

    consumer.alRegistrarUsuario(
        new UsuarioRegistrado(
            cuentaId, "clerk-user-1", "ana@correo.com", Instant.parse("2026-06-24T10:00:00Z")));

    assertEquals(cuentaId, repository.onboardingPendiente.cuentaId());
    assertEquals("PENDIENTE_COMPLETAR", repository.onboardingPendiente.estado());
    assertTrue(repository.onboardingPendiente.id() != null);
    assertEquals(1, repository.registrosGuardados);
  }

  @Test
  void usuarioRegistradoDuplicadoNoDuplicaOnboardingPendiente() {
    PerfilOnboardingPendienteRepositoryFake repository =
        new PerfilOnboardingPendienteRepositoryFake();
    RegistrarOnboardingPerfilPendienteUseCase useCase =
        new RegistrarOnboardingPerfilPendienteUseCase(repository);
    AccesoEventConsumer consumer = new AccesoEventConsumer(useCase);
    UUID cuentaId = UUID.randomUUID();
    UsuarioRegistrado evento =
        new UsuarioRegistrado(
            cuentaId, "clerk-user-1", "ana@correo.com", Instant.parse("2026-06-24T10:00:00Z"));

    consumer.alRegistrarUsuario(evento);
    consumer.alRegistrarUsuario(evento);

    assertEquals(cuentaId, repository.onboardingPendiente.cuentaId());
    assertEquals(1, repository.registrosGuardados);
  }

  private static final class PerfilOnboardingPendienteRepositoryFake
      implements PerfilOnboardingPendienteRepository {

    private PerfilOnboardingPendiente onboardingPendiente;
    private int registrosGuardados;

    @Override
    public void guardar(PerfilOnboardingPendiente onboardingPendiente) {
      this.onboardingPendiente = onboardingPendiente;
      registrosGuardados++;
    }

    @Override
    public boolean existePorCuentaId(UUID cuentaId) {
      return onboardingPendiente != null && onboardingPendiente.cuentaId().equals(cuentaId);
    }

    @Override
    public void eliminarPorCuentaId(UUID cuentaId) {
      if (onboardingPendiente != null && onboardingPendiente.cuentaId().equals(cuentaId)) {
        onboardingPendiente = null;
      }
    }
  }
}
