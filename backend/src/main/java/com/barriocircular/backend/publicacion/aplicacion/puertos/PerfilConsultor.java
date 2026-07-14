package com.barriocircular.backend.publicacion.aplicacion.puertos;

import com.barriocircular.backend.publicacion.aplicacion.dto.InfoContactoCreador;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import java.util.Optional;
import java.util.UUID;

public interface PerfilConsultor {

  Optional<PerfilCapacidades> obtenerCapacidadesPorClerkId(String clerkId);

  Optional<InfoContactoCreador> obtenerInfoContactoPorPerfilId(UUID perfilId);
}
