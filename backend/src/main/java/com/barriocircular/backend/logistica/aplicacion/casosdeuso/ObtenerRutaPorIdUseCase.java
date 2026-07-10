package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ObtenerRutaPorIdUseCase {

  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final ReservasCatalogoPort reservasCatalogoPort;

  public ObtenerRutaPorIdUseCase(
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort,
      ReservasCatalogoPort reservasCatalogoPort) {
    this.almacenRutaRecoleccionPort =
        Objects.requireNonNull(almacenRutaRecoleccionPort, "El almacen de rutas es obligatorio.");
    this.reservasCatalogoPort =
        Objects.requireNonNull(reservasCatalogoPort, "El puerto de reservas es obligatorio.");
  }

  public Optional<RutaRecoleccionResultado> ejecutar(UUID rutaId) {
    Objects.requireNonNull(rutaId, "El id de la ruta es obligatorio.");
    return almacenRutaRecoleccionPort
        .buscarPorId(rutaId)
        .map(
            ruta ->
                RutaRecoleccionResultado.desde(
                    ruta,
                    reservasCatalogoPort.obtenerReservasActivasPorReciclador(
                        ruta.recicladorId().valor())));
  }
}
