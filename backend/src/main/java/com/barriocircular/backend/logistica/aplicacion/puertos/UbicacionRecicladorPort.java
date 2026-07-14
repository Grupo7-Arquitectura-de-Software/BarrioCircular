package com.barriocircular.backend.logistica.aplicacion.puertos;

import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import java.util.Optional;
import java.util.UUID;

public interface UbicacionRecicladorPort {

  Optional<CoordenadaGPS> obtenerUbicacionActual(UUID recicladorId);
}
