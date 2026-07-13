package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.dto.InfoContactoCreador;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final PerfilConsultor perfilConsultor;

  public ObtenerPublicacionUseCase(
      PublicacionRepositorio publicacionRepositorio,
      @Qualifier("perfilConsultorPublicacion") PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional(readOnly = true)
  public PublicacionResultado ejecutar(UUID publicacionId) {
    return publicacionRepositorio
        .buscarPorId(PublicacionId.de(publicacionId))
        .map(
            publicacion -> {
              InfoContactoCreador contacto =
                  perfilConsultor
                      .obtenerInfoContactoPorPerfilId(publicacion.creador().valor())
                      .orElse(null);
              String nombre = contacto != null ? contacto.nombre() : null;
              String telefono = contacto != null ? contacto.telefono() : null;
              return PublicacionResultado.desde(publicacion, nombre, telefono);
            })
        .orElseThrow(PublicacionNoEncontradaException::new);
  }
}
