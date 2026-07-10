package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.comandos.ActualizarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.modelo.DetalleMaterial;
import com.barriocircular.backend.publicacion.dominio.modelo.EvidenciaVisual;
import com.barriocircular.backend.publicacion.dominio.modelo.PesoEstimado;
import com.barriocircular.backend.publicacion.dominio.modelo.PrecioPorKilo;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.modelo.UbicacionRecogida;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final PerfilConsultor perfilConsultor;

  public ActualizarPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio, PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional
  public PublicacionResultado ejecutar(
      ActualizarPublicacionCommand command, String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    PublicacionId id = PublicacionId.de(command.publicacionId());

    Publicacion publicacion =
        publicacionRepositorio.buscarPorId(id).orElseThrow(PublicacionNoEncontradaException::new);

    if (!publicacion.creador().valor().equals(perfil.perfilId())) {
      throw new PerfilNoAutorizadoException("Solo el creador puede editar la publicación.");
    }

    DetalleMaterial detalle =
        new DetalleMaterial(
            TipoResiduo.valueOf(command.tipoResiduo()), PesoEstimado.deKilos(command.pesoKg()));
    PrecioPorKilo precioPorKilo = new PrecioPorKilo(command.precioPorKilo());
    UbicacionRecogida ubicacion = new UbicacionRecogida(command.latitud(), command.longitud());
    EvidenciaVisual evidencia = new EvidenciaVisual(command.evidenciaUrl());

    publicacion.actualizarDatos(detalle, precioPorKilo, ubicacion, evidencia);

    publicacionRepositorio.guardar(publicacion);

    return PublicacionResultado.desde(publicacion);
  }
}
