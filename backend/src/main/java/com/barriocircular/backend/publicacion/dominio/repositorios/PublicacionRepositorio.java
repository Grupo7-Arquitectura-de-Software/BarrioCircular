package com.barriocircular.backend.publicacion.dominio.repositorios;

import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;

import java.util.Optional;

public interface PublicacionRepositorio {

    Publicacion guardar(Publicacion publicacion);

    Optional<Publicacion> buscarPorId(PublicacionId id);

    boolean existePorId(PublicacionId id);
}
