package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarMisReservasUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final PerfilConsultor perfilConsultor;

  public ListarMisReservasUseCase(
      PublicacionRepositorio publicacionRepositorio, PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional(readOnly = true)
  public List<PublicacionResultado> ejecutar(String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    return publicacionRepositorio.listarPorReservador(ReservadorId.de(perfil.perfilId())).stream()
        .map(PublicacionResultado::desde)
        .toList();
  }
}
