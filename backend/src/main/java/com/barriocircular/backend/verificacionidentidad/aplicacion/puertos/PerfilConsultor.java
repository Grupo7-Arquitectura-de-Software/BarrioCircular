package com.barriocircular.backend.verificacionidentidad.aplicacion.puertos;

import java.util.Optional;
import java.util.UUID;

public interface PerfilConsultor {

  Optional<PerfilElegible> obtenerPorClerkId(String clerkId);

  Optional<PerfilVerificable> obtenerPorPerfilId(UUID perfilId);
}
