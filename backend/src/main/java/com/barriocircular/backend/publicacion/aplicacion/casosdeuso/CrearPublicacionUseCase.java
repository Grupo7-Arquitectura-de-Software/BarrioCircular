package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.comandos.CrearPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.DetalleMaterial;
import com.barriocircular.backend.publicacion.dominio.modelo.EvidenciaVisual;
import com.barriocircular.backend.publicacion.dominio.modelo.PesoEstimado;
import com.barriocircular.backend.publicacion.dominio.modelo.PrecioPorKilo;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.modelo.UbicacionRecogida;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final ApplicationEventPublisher eventPublisher;
  private final PerfilConsultor perfilConsultor;

  public CrearPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio,
      ApplicationEventPublisher eventPublisher,
      PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.eventPublisher = eventPublisher;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional
  public PublicacionResultado ejecutar(CrearPublicacionCommand command, String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    if (!perfil.puedePublicarMateriales()) {
      throw new PerfilNoAutorizadoException(
          "El perfil autenticado no esta autorizado para publicar materiales.");
    }

    PublicacionId id = PublicacionId.nuevo();
    CiudadanoId creador = CiudadanoId.de(perfil.perfilId());
    DetalleMaterial detalle =
        new DetalleMaterial(
            TipoResiduo.valueOf(command.tipoResiduo()), PesoEstimado.deKilos(command.pesoKg()));
    PrecioPorKilo precioPorKilo = new PrecioPorKilo(command.precioPorKilo());
    UbicacionRecogida ubicacion = new UbicacionRecogida(command.latitud(), command.longitud());
    EvidenciaVisual evidencia = new EvidenciaVisual(command.evidenciaUrl());

    Publicacion publicacion =
        Publicacion.crear(id, creador, detalle, precioPorKilo, ubicacion, evidencia);

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
