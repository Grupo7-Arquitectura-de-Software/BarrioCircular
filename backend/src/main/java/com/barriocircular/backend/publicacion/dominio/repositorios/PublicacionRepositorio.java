package com.barriocircular.backend.publicacion.dominio.repositorios;

import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import java.util.List;
import java.util.Optional;

public interface PublicacionRepositorio {

  Publicacion guardar(Publicacion publicacion);

  Optional<Publicacion> buscarPorId(PublicacionId id);

  boolean existePorId(PublicacionId id);

  List<Publicacion> listarPorEstado(EstadoPublicacion estado);

  List<Publicacion> listarPorCreador(CiudadanoId creadorId);

  List<Publicacion> listarPorReservador(ReservadorId reservadorId);
}
