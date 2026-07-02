package com.barriocircular.backend.perfiles.infraestructura.mensajeria;

import com.barriocircular.backend.acceso.dominio.eventos.UsuarioRegistrado;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.RegistrarOnboardingPerfilPendienteUseCase;
import com.barriocircular.backend.perfiles.aplicacion.comandos.RegistrarOnboardingPerfilPendienteCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccesoEventConsumer {

  private final RegistrarOnboardingPerfilPendienteUseCase registrarOnboardingPerfilPendienteUseCase;

  public AccesoEventConsumer(
      RegistrarOnboardingPerfilPendienteUseCase registrarOnboardingPerfilPendienteUseCase) {
    this.registrarOnboardingPerfilPendienteUseCase = registrarOnboardingPerfilPendienteUseCase;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void alRegistrarUsuario(UsuarioRegistrado evento) {
    registrarOnboardingPerfilPendienteUseCase.ejecutar(
        new RegistrarOnboardingPerfilPendienteCommand(
            evento.cuentaId(), evento.clerkId(), evento.correoElectronico(), evento.ocurridoEn()));
  }
}
