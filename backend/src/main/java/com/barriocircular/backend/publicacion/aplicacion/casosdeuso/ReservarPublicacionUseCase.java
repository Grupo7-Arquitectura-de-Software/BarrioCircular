package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.publicacion.aplicacion.comandos.ReservarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservarPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final ApplicationEventPublisher eventPublisher;
  private final PerfilConsultor perfilConsultor;
  private final PerfilUsuarioRepository repositorioPerfiles;

  public ReservarPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio,
      ApplicationEventPublisher eventPublisher,
      PerfilConsultor perfilConsultor,
      PerfilUsuarioRepository repositorioPerfiles) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.eventPublisher = eventPublisher;
    this.perfilConsultor = perfilConsultor;
    this.repositorioPerfiles = repositorioPerfiles;
  }

  @Transactional
  public PublicacionResultado ejecutar(
      ReservarPublicacionCommand command, String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    if (!perfil.puedeComprarMateriales()) {
      throw new PerfilNoAutorizadoException(
          "El perfil autenticado no esta autorizado para reservar materiales.");
    }

    PublicacionId id = PublicacionId.de(command.publicacionId());

    Publicacion publicacion =
        publicacionRepositorio.buscarPorId(id).orElseThrow(PublicacionNoEncontradaException::new);

    if ("CENTRO_RECOLECCION".equals(perfil.rol())) {
      String rolCreador =
          repositorioPerfiles
              .buscarPorId(publicacion.creador().valor())
              .map(p -> p.getRol().name())
              .orElse(null);
      if (!"RECICLADOR".equals(rolCreador)) {
        throw new PerfilNoAutorizadoException(
            "Los centros de recoleccion solo pueden reservar publicaciones de recicladores.");
      }
    }

    publicacion.reservarPublicacionPor(ReservadorId.de(perfil.perfilId()));

    publicacionRepositorio.guardar(publicacion);
    publicarEventos(publicacion);

    return PublicacionResultado.desde(publicacion);
  }

  private void publicarEventos(Publicacion publicacion) {
    for (EventoDominio evento : publicacion.eventos()) {
      eventPublisher.publishEvent(evento);
    }
    publicacion.limpiarEventos();
  }
}
