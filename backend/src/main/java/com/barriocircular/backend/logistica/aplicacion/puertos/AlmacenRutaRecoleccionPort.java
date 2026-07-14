package com.barriocircular.backend.logistica.aplicacion.puertos;

import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import java.util.Optional;
import java.util.UUID;

public interface AlmacenRutaRecoleccionPort {

  RutaRecoleccion guardar(RutaRecoleccion ruta);

  Optional<RutaRecoleccion> buscarPorId(UUID rutaId);

  Optional<RutaRecoleccion> obtenerRutaActivaPorReciclador(UUID recicladorId);
}
