package com.barriocircular.backend.logistica.dominio.servicios;

import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.RecicladorId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.DuracionViaje;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.dominio.objetosValor.TiempoEstimadoLlegada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PlanificadorRutaRecoleccion {

  // Regla de negocio MVP: cada parada consume 10 minutos de atencion antes de continuar.
  private static final int MINUTOS_SERVICIO_POR_PARADA_MVP = 10;
  private static final double VELOCIDAD_PROMEDIO_URBANA_KM_H = 25.0;

  private final CalculadorDistanciaGeografica calculadorDistancia;

  public PlanificadorRutaRecoleccion(CalculadorDistanciaGeografica calculadorDistancia) {
    this.calculadorDistancia =
        Objects.requireNonNull(calculadorDistancia, "El calculador de distancia es obligatorio.");
  }

  public RutaRecoleccion planificar(
      RecicladorId recicladorId,
      CoordenadaGPS origen,
      List<DestinoRecoleccion> destinos,
      LocalDate fecha,
      LocalTime horaInicio) {
    validarSolicitud(origen, destinos, fecha, horaInicio);

    List<DestinoRecoleccion> destinosOrdenados = ordenarPorVecinoMasCercano(origen, destinos);
    List<ParadaRecoleccion> paradas = generarParadas(destinosOrdenados, origen, fecha, horaInicio);

    return RutaRecoleccion.planificar(recicladorId, fecha, horaInicio, paradas);
  }

  private void validarSolicitud(
      CoordenadaGPS origen,
      List<DestinoRecoleccion> destinos,
      LocalDate fecha,
      LocalTime horaInicio) {
    Objects.requireNonNull(origen, "El origen de la ruta es obligatorio.");
    Objects.requireNonNull(destinos, "Los destinos de recoleccion son obligatorios.");
    Objects.requireNonNull(fecha, "La fecha de la ruta es obligatoria.");
    Objects.requireNonNull(horaInicio, "La hora de inicio de la ruta es obligatoria.");
    if (destinos.isEmpty()) {
      throw new IllegalArgumentException("La ruta requiere al menos un destino.");
    }
    destinos.forEach(destino -> Objects.requireNonNull(destino, "El destino no puede ser nulo."));
  }

  private List<DestinoRecoleccion> ordenarPorVecinoMasCercano(
      CoordenadaGPS origen, List<DestinoRecoleccion> destinos) {
    List<DestinoRecoleccion> pendientes = new ArrayList<>(destinos);
    List<DestinoRecoleccion> ordenados = new ArrayList<>();
    CoordenadaGPS posicionActual = origen;

    while (!pendientes.isEmpty()) {
      DestinoRecoleccion siguiente = destinoMasCercano(posicionActual, pendientes);
      ordenados.add(siguiente);
      pendientes.remove(siguiente);
      posicionActual = siguiente.ubicacion();
    }

    return ordenados;
  }

  private DestinoRecoleccion destinoMasCercano(
      CoordenadaGPS posicionActual, List<DestinoRecoleccion> pendientes) {
    return pendientes.stream()
        .min(Comparator.comparingDouble(destino -> distanciaDesde(posicionActual, destino)))
        .orElseThrow();
  }

  private double distanciaDesde(CoordenadaGPS posicionActual, DestinoRecoleccion destino) {
    return calculadorDistancia.distanciaKm(posicionActual, destino.ubicacion());
  }

  private List<ParadaRecoleccion> generarParadas(
      List<DestinoRecoleccion> destinosOrdenados,
      CoordenadaGPS origen,
      LocalDate fecha,
      LocalTime horaInicio) {
    List<ParadaRecoleccion> paradas = new ArrayList<>();
    CoordenadaGPS posicionActual = origen;
    long minutosAcumulados = 0;

    for (int indice = 0; indice < destinosOrdenados.size(); indice++) {
      DestinoRecoleccion destino = destinosOrdenados.get(indice);
      minutosAcumulados += calcularMinutosViaje(posicionActual, destino.ubicacion()).minutos();
      TiempoEstimadoLlegada tiempoEstimado = TiempoEstimadoLlegada.deMinutos(minutosAcumulados);
      HorarioParada horarioEstimado =
          HorarioParada.de(fecha, horaInicio)
              .mas(DuracionViaje.deMinutos(tiempoEstimado.minutosDesdeInicioRuta()));

      paradas.add(
          ParadaRecoleccion.programar(
              destino.publicacionId(), destino.ubicacion(), indice + 1, horarioEstimado));

      minutosAcumulados += MINUTOS_SERVICIO_POR_PARADA_MVP;
      posicionActual = destino.ubicacion();
    }

    return paradas;
  }

  private DuracionViaje calcularMinutosViaje(CoordenadaGPS origen, CoordenadaGPS destino) {
    double distanciaKm = calculadorDistancia.distanciaKm(origen, destino);
    long minutos = Math.max(1, Math.round((distanciaKm / VELOCIDAD_PROMEDIO_URBANA_KM_H) * 60));
    return DuracionViaje.deMinutos(minutos);
  }

  public int minutosServicioPorParadaMvp() {
    return MINUTOS_SERVICIO_POR_PARADA_MVP;
  }
}
