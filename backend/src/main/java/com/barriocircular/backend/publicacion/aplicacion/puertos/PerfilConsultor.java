package com.barriocircular.backend.publicacion.aplicacion.puertos;

import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import java.util.Optional;

public interface PerfilConsultor {

  Optional<PerfilCapacidades> obtenerCapacidadesPorClerkId(String clerkId);
}
