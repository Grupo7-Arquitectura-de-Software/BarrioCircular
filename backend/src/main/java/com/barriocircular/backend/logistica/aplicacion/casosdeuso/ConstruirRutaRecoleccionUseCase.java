package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.servicios.CalculadorDistanciaGeografica;
import com.barriocircular.backend.logistica.dominio.servicios.DestinoRecoleccion;
import com.barriocircular.backend.logistica.dominio.servicios.PlanificadorRutaRecoleccion;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConstruirRutaRecoleccionUseCase {

  private final ReservasCatalogoPort reservasCatalogoPort;
  private final UbicacionRecicladorPort ubicacionRecicladorPort;
  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final PlanificadorRutaRecoleccion planificadorRuta;

  @Autowired
  public ConstruirRutaRecoleccionUseCase(
      ReservasCatalogoPort reservasCatalogoPort,
      UbicacionRecicladorPort ubicacionRecicladorPort,
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort) {
    this(
        reservasCatalogoPort,
        ubicacionRecicladorPort,
        almacenRutaRecoleccionPort,
        new PlanificadorRutaRecoleccion(new CalculadorDistanciaGeografica()));
  }

  ConstruirRutaRecoleccionUseCase(
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

  public RutaRecoleccionResultado ejecutar(
      UUID recicladorId, LocalTime horaInicioRuta, LocalDate fechaRuta) {
    validarEntrada(recicladorId, fechaRuta, horaInicioRuta);

    List<ReservaCatalogo> reservas =
        reservasCatalogoPort.obtenerReservasActivasPorReciclador(recicladorId);
    if (reservas.isEmpty()) {
      throw new IllegalStateException("No existen reservas activas para construir la ruta.");
    }

    CoordenadaGPS ubicacionReciclador =
        ubicacionRecicladorPort
            .obtenerUbicacionActual(recicladorId)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No existe ubicacion actual para el reciclador solicitado."));

    RutaRecoleccion ruta =
        planificadorRuta.planificar(
            RecicladorId.de(recicladorId),
            ubicacionReciclador,
            convertirDestinos(reservas),
            fechaRuta,
            horaInicioRuta);

    RutaRecoleccion guardada = almacenRutaRecoleccionPort.guardar(ruta);
    return RutaRecoleccionResultado.desde(guardada, reservas, ubicacionReciclador);
  }

  private void validarEntrada(UUID recicladorId, LocalDate fechaRuta, LocalTime horaInicioRuta) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");
    Objects.requireNonNull(fechaRuta, "La fecha de la ruta es obligatoria.");
    Objects.requireNonNull(horaInicioRuta, "La hora de inicio de la ruta es obligatoria.");
  }

  private List<DestinoRecoleccion> convertirDestinos(List<ReservaCatalogo> reservas) {
    return reservas.stream().map(this::convertirDestino).toList();
  }

  private DestinoRecoleccion convertirDestino(ReservaCatalogo reserva) {
    return new DestinoRecoleccion(
        PublicacionId.de(reserva.publicacionId()),
        new CoordenadaGPS(reserva.latitud(), reserva.longitud()));
  }
}
