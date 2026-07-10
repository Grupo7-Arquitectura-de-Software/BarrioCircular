package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.comandos.FinalizarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinalizarPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final ApplicationEventPublisher eventPublisher;
  private final PerfilConsultor perfilConsultor;

  public FinalizarPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio,
      ApplicationEventPublisher eventPublisher,
      PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.eventPublisher = eventPublisher;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional
  public PublicacionResultado ejecutar(
      FinalizarPublicacionCommand command, String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    PublicacionId id = PublicacionId.de(command.publicacionId());

    Publicacion publicacion =
        publicacionRepositorio.buscarPorId(id).orElseThrow(PublicacionNoEncontradaException::new);

    if (!esCreadorOReservador(publicacion, perfil)) {
      throw new PerfilNoAutorizadoException(
          "Solo el creador o el reservador de la publicación pueden finalizarla.");
    }

    publicacion.finalizar();

    publicacionRepositorio.guardar(publicacion);
    publicarEventos(publicacion);

    return PublicacionResultado.desde(publicacion);
  }

  private boolean esCreadorOReservador(Publicacion publicacion, PerfilCapacidades perfil) {
    boolean esCreador = publicacion.creador().valor().equals(perfil.perfilId());
    boolean esReservador =
        publicacion.reservadoPor() != null
            && publicacion.reservadoPor().valor().equals(perfil.perfilId());
    return esCreador || esReservador;
  }

  private void publicarEventos(Publicacion publicacion) {
    for (EventoDominio evento : publicacion.eventos()) {
      eventPublisher.publishEvent(evento);
    }
    publicacion.limpiarEventos();
  }
}
