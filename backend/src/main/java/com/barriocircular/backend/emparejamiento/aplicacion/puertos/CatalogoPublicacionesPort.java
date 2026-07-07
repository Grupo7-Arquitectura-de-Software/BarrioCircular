package com.barriocircular.backend.emparejamiento.aplicacion.puertos;

import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;

import java.util.List;

public interface CatalogoPublicacionesPort {

    List<OfertaCatalogo> obtenerCatalogoDisponible();
}
