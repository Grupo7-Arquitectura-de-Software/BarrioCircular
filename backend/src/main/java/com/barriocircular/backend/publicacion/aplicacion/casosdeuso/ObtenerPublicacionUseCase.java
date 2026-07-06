package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPublicacionUseCase {

  private final PublicacionRepositorio publicacionRepositorio;

  public ObtenerPublicacionUseCase(PublicacionRepositorio publicacionRepositorio) {
    this.publicacionRepositorio = publicacionRepositorio;
  }

  @Transactional(readOnly = true)
  public PublicacionResultado ejecutar(UUID publicacionId) {
    return publicacionRepositorio
        .buscarPorId(PublicacionId.de(publicacionId))
        .map(PublicacionResultado::desde)
        .orElseThrow(PublicacionNoEncontradaException::new);
  }
}
