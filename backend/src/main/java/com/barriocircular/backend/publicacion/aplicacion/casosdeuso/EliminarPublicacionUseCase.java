package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.comandos.EliminarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final ApplicationEventPublisher eventPublisher;
  private final PerfilConsultor perfilConsultor;

  public EliminarPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio,
      ApplicationEventPublisher eventPublisher,
      PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.eventPublisher = eventPublisher;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional
  public void ejecutar(EliminarPublicacionCommand command, String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    PublicacionId id = PublicacionId.de(command.publicacionId());

    Publicacion publicacion =
        publicacionRepositorio.buscarPorId(id).orElseThrow(PublicacionNoEncontradaException::new);

    if (!publicacion.creador().valor().equals(perfil.perfilId())) {
      throw new PerfilNoAutorizadoException("Solo el creador puede eliminar la publicación.");
    }

    if (publicacion.estado() != EstadoPublicacion.DISPONIBLE) {
      throw new EstadoInvalidoException(
          "Solo se puede eliminar una publicación mientras está DISPONIBLE.");
    }

    publicacion.cancelar();

    publicacionRepositorio.guardar(publicacion);
    publicarEventos(publicacion);
  }

  private void publicarEventos(Publicacion publicacion) {
    for (EventoDominio evento : publicacion.eventos()) {
      eventPublisher.publishEvent(evento);
    }
    publicacion.limpiarEventos();
  }
}
