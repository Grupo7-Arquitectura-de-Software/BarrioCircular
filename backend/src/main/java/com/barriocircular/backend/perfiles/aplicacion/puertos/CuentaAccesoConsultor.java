package com.barriocircular.backend.perfiles.aplicacion.puertos;

import java.util.Optional;
import java.util.UUID;

public interface CuentaAccesoConsultor {

  Optional<UUID> obtenerCuentaIdPorClerkId(String clerkId);
}
