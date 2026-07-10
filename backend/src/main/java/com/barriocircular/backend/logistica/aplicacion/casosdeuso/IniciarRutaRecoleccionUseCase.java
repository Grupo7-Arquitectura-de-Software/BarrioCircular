package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IniciarRutaRecoleccionUseCase {

  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final ReservasCatalogoPort reservasCatalogoPort;
  private final UbicacionRecicladorPort ubicacionRecicladorPort;

  @Autowired
  public IniciarRutaRecoleccionUseCase(
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort,
      ReservasCatalogoPort reservasCatalogoPort,
      UbicacionRecicladorPort ubicacionRecicladorPort) {
    this.almacenRutaRecoleccionPort =
        Objects.requireNonNull(almacenRutaRecoleccionPort, "El almacen de rutas es obligatorio.");
    this.reservasCatalogoPort =
        Objects.requireNonNull(reservasCatalogoPort, "El puerto de reservas es obligatorio.");
    this.ubicacionRecicladorPort =
        Objects.requireNonNull(ubicacionRecicladorPort, "El puerto de ubicacion es obligatorio.");
  }

  public RutaRecoleccionResultado ejecutar(UUID recicladorId) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");

    com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion rutaActiva =
        almacenRutaRecoleccionPort
            .obtenerRutaActivaPorReciclador(recicladorId)
            .orElseThrow(
                () -> new IllegalStateException("No existe una ruta activa para el reciclador."));

    if (rutaActiva.estado() == EstadoRutaRecoleccion.PLANIFICADA) {
      rutaActiva.iniciar();
    }

    com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion rutaGuardada =
        almacenRutaRecoleccionPort.guardar(rutaActiva);

    List<ReservaCatalogo> reservas = reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId);
    CoordenadaGPS origen = obtenerUbicacionReciclador(recicladorId);
    return RutaRecoleccionResultado.desde(rutaGuardada, reservas, origen);
  }

  private CoordenadaGPS obtenerUbicacionReciclador(UUID recicladorId) {
    return ubicacionRecicladorPort
        .obtenerUbicacionActual(recicladorId)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "No existe ubicacion actual para el reciclador solicitado."));
  }
}
