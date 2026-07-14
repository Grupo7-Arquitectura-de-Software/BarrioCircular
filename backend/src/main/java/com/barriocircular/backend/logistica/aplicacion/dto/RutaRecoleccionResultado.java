package com.barriocircular.backend.logistica.aplicacion.dto;

import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public record RutaRecoleccionResultado(
    UUID rutaId,
    String estado,
    LocalDate fecha,
    CoordenadaRutaResultado origen,
    List<ParadaRecoleccionResultado> paradas) {

  public RutaRecoleccionResultado(
      UUID rutaId, String estado, LocalDate fecha, List<ParadaRecoleccionResultado> paradas) {
    this(rutaId, estado, fecha, null, paradas);
  }

  public RutaRecoleccionResultado {
    Objects.requireNonNull(rutaId, "El id de la ruta es obligatorio.");
    if (estado == null || estado.isBlank()) {
      throw new IllegalArgumentException("El estado de la ruta es obligatorio.");
    }
    Objects.requireNonNull(fecha, "La fecha de la ruta es obligatoria.");
    paradas = List.copyOf(Objects.requireNonNull(paradas, "Las paradas son obligatorias."));
  }

  public static RutaRecoleccionResultado desde(RutaRecoleccion ruta) {
    Objects.requireNonNull(ruta, "La ruta a convertir es obligatoria.");
    return new RutaRecoleccionResultado(
        ruta.id().valor(),
        ruta.estado().name(),
        ruta.fecha(),
        ruta.paradas().stream().map(ParadaRecoleccionResultado::desde).toList());
  }

  public static RutaRecoleccionResultado desde(
      RutaRecoleccion ruta, List<ReservaCatalogo> reservas) {
    return desde(ruta, reservas, null);
  }

  public static RutaRecoleccionResultado desde(
      RutaRecoleccion ruta, List<ReservaCatalogo> reservas, CoordenadaGPS origen) {
    Objects.requireNonNull(ruta, "La ruta a convertir es obligatoria.");
    Objects.requireNonNull(reservas, "Las reservas para enriquecer la ruta son obligatorias.");
    Map<UUID, ReservaCatalogo> reservasPorPublicacion =
        reservas.stream()
            .collect(Collectors.toMap(ReservaCatalogo::publicacionId, Function.identity()));
    return new RutaRecoleccionResultado(
        ruta.id().valor(),
        ruta.estado().name(),
        ruta.fecha(),
        origen == null ? null : CoordenadaRutaResultado.desde(origen),
        ruta.paradas().stream()
            .map(
                parada ->
                    ParadaRecoleccionResultado.desde(
                        parada, reservasPorPublicacion.get(parada.publicacionId().valor())))
            .toList());
  }
}
