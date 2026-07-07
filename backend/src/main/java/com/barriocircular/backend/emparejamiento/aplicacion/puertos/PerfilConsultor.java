package com.barriocircular.backend.emparejamiento.aplicacion.puertos;

import com.barriocircular.backend.emparejamiento.aplicacion.dto.PerfilCapacidadesComprador;

import java.util.Optional;


public interface PerfilConsultor {

    Optional<PerfilCapacidadesComprador> obtenerCapacidadesPorClerkId(String clerkId);
}
