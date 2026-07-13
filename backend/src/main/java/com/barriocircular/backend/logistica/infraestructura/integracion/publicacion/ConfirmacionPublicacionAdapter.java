package com.barriocircular.backend.logistica.infraestructura.integracion.publicacion;

import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionPublicacionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.ConfirmacionPublicacionPort;
import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.Objects;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ConfirmacionPublicacionAdapter implements ConfirmacionPublicacionPort {

  private final PublicacionRepositorio publicacionRepositorio;
  private final ApplicationEventPublisher eventPublisher;

  public ConfirmacionPublicacionAdapter(
      PublicacionRepositorio publicacionRepositorio, ApplicationEventPublisher eventPublisher) {
    this.publicacionRepositorio =
        Objects.requireNonNull(
            publicacionRepositorio, "El repositorio de publicaciones es obligatorio.");
    this.eventPublisher =
        Objects.requireNonNull(eventPublisher, "El publicador de eventos es obligatorio.");
  }

  @Override
  public ConfirmacionPublicacionResultado confirmarRecoleccion(
      UUID publicacionId, UUID recolectorId, double pesoRealVerificado, String observaciones) {
    Objects.requireNonNull(publicacionId, "El id de la publicacion es obligatorio.");
    Objects.requireNonNull(recolectorId, "El id del recolector es obligatorio.");

    Publicacion publicacion =
        publicacionRepositorio
            .buscarPorId(PublicacionId.de(publicacionId))
            .orElseThrow(() -> new IllegalStateException("No existe la publicacion solicitada."));

    if (publicacion.estado() != EstadoPublicacion.RESERVADA) {
      throw new IllegalStateException("La publicacion debe estar reservada para confirmarse.");
    }
    if (publicacion.reservadoPor() == null
        || !publicacion.reservadoPor().valor().equals(recolectorId)) {
      throw new IllegalStateException(
          "La publicacion no fue reservada por el recolector autenticado.");
    }

    publicacion.finalizarConVerificacion(pesoRealVerificado, observaciones);
    Publicacion guardada = publicacionRepositorio.guardar(publicacion);
    publicarEventos(publicacion);

    return new ConfirmacionPublicacionResultado(
        guardada.id().valor(),
        guardada.estado().name(),
        guardada.pesoRealVerificado(),
        guardada.observacionesVerificacion());
  }

  private void publicarEventos(Publicacion publicacion) {
    for (EventoDominio evento : publicacion.eventos()) {
      eventPublisher.publishEvent(evento);
    }
    publicacion.limpiarEventos();
  }
}
