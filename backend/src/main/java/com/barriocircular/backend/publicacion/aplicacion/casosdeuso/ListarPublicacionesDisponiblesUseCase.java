package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarPublicacionesDisponiblesUseCase {

  private final PublicacionRepositorio publicacionRepositorio;

  public ListarPublicacionesDisponiblesUseCase(PublicacionRepositorio publicacionRepositorio) {
    this.publicacionRepositorio = publicacionRepositorio;
  }

  @Transactional(readOnly = true)
  public List<PublicacionResultado> ejecutar() {
    return publicacionRepositorio.listarPorEstado(EstadoPublicacion.DISPONIBLE).stream()
        .map(PublicacionResultado::desde)
        .toList();
  }
}
