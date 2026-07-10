package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActualizarRutaRecoleccionUseCase {

  private final ReservasCatalogoPort reservasCatalogoPort;
  private final UbicacionRecicladorPort ubicacionRecicladorPort;
  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final PlanificadorRutaRecoleccion planificadorRuta;

  @Autowired
  public ActualizarRutaRecoleccionUseCase(
      ReservasCatalogoPort reservasCatalogoPort,
      UbicacionRecicladorPort ubicacionRecicladorPort,
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort) {
    this(
        reservasCatalogoPort,
        ubicacionRecicladorPort,
        almacenRutaRecoleccionPort,
        new PlanificadorRutaRecoleccion(
            new com.barriocircular.backend.logistica.dominio.servicios
                .CalculadorDistanciaGeografica()));
  }

  ActualizarRutaRecoleccionUseCase(
      ReservasCatalogoPort reservasCatalogoPort,
      UbicacionRecicladorPort ubicacionRecicladorPort,
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort,
      PlanificadorRutaRecoleccion planificadorRuta) {
    this.reservasCatalogoPort =
        Objects.requireNonNull(reservasCatalogoPort, "El puerto de reservas es obligatorio.");
    this.ubicacionRecicladorPort =
        Objects.requireNonNull(
            ubicacionRecicladorPort, "El puerto de ubicacion del reciclador es obligatorio.");
    this.almacenRutaRecoleccionPort =
        Objects.requireNonNull(almacenRutaRecoleccionPort, "El almacen de rutas es obligatorio.");
    this.planificadorRuta =
        Objects.requireNonNull(planificadorRuta, "El planificador de ruta es obligatorio.");
  }

  public RutaRecoleccionResultado ejecutar(UUID recicladorId) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");

    RutaRecoleccion rutaActiva =
        almacenRutaRecoleccionPort
            .obtenerRutaActivaPorReciclador(recicladorId)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No existe una ruta activa para el reciclador solicitado."));

    List<ReservaCatalogo> reservas =
        reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId);
    if (reservas.isEmpty()) {
      throw new IllegalStateException("No existen reservas activas para actualizar la ruta.");
    }

    CoordenadaGPS ubicacionReciclador =
        ubicacionRecicladorPort
            .obtenerUbicacionActual(recicladorId)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No existe ubicacion actual para el reciclador solicitado."));

    RutaRecoleccion rutaReplanificada =
        planificadorRuta.planificar(
            RecicladorId.de(recicladorId),
            ubicacionReciclador,
            convertirDestinos(reservas),
            rutaActiva.fecha(),
            rutaActiva.horaInicio());

    rutaActiva.replanificar(rutaReplanificada.paradas());
    RutaRecoleccion rutaGuardada = almacenRutaRecoleccionPort.guardar(rutaActiva);
    return RutaRecoleccionResultado.desde(rutaGuardada, reservas, ubicacionReciclador);
  }

  private List<com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion>
      convertirDestinos(List<ReservaCatalogo> reservas) {
    return reservas.stream().map(this::convertirDestino).toList();
  }

  private com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion
      convertirDestino(ReservaCatalogo reserva) {
    return new com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion(
        com.barriocircular.backend.logistica.dominio.modelo.PublicacionId.de(
            reserva.publicacionId()),
        new CoordenadaGPS(reserva.latitud(), reserva.longitud()));
  }
}
