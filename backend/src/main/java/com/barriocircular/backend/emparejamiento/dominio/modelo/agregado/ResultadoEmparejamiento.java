package com.barriocircular.backend.emparejamiento.dominio.modelo.agregado;

import com.barriocircular.backend.emparejamiento.dominio.eventos.EmparejamientoCalculado;
import com.barriocircular.backend.emparejamiento.dominio.eventos.EventoDominio;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.PosicionFueraDeRangoException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CompradorId;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PreferenciaFiltro;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PuntajeOferta;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ResultadoEmparejamiento {
  private static final double LATITUD_MINIMA_QUITO = -0.50;
  private static final double LATITUD_MAXIMA_QUITO = 0.10;
  private static final double LONGITUD_MINIMA_QUITO = -78.70;
  private static final double LONGITUD_MAXIMA_QUITO = -78.20;

  private final UUID id;
  private final CompradorId compradorId;
  private final CoordenadaGPS posicionOrigen;
  private final PreferenciaFiltro filtroAplicado;
  private final List<PuntajeOferta> ofertasOrdenadas;
  private final Instant fechaCalculo;

  private final transient List<EventoDominio> eventos = new ArrayList<>();

  private ResultadoEmparejamiento(
      UUID id,
      CompradorId compradorId,
      CoordenadaGPS posicionOrigen,
      PreferenciaFiltro filtroAplicado,
      List<PuntajeOferta> ofertasOrdenadas,
      Instant fechaCalculo) {
    this.id = id;
    this.compradorId = compradorId;
    this.posicionOrigen = posicionOrigen;
    this.filtroAplicado = filtroAplicado;
    this.ofertasOrdenadas = ofertasOrdenadas;
    this.fechaCalculo = fechaCalculo;
  }

  public static ResultadoEmparejamiento calcular(
      CompradorId compradorId,
      CoordenadaGPS posicionOrigen,
      PreferenciaFiltro filtroAplicado,
      List<PuntajeOferta> ofertasOrdenadas) {
    Objects.requireNonNull(compradorId, "El compradorId es obligatorio.");
    Objects.requireNonNull(posicionOrigen, "La posicion de origen es obligatoria.");
    Objects.requireNonNull(filtroAplicado, "El filtro aplicado es obligatorio.");
    Objects.requireNonNull(ofertasOrdenadas, "La lista de ofertas ordenadas no puede ser nula.");

    validarPosicionDentroDeQuito(posicionOrigen);

    UUID id = UUID.randomUUID();
    Instant fechaCalculo = Instant.now();
    List<PuntajeOferta> ofertasInmutables = List.copyOf(ofertasOrdenadas);

    ResultadoEmparejamiento resultado =
        new ResultadoEmparejamiento(
            id, compradorId, posicionOrigen, filtroAplicado, ofertasInmutables, fechaCalculo);

    resultado.registrar(
        new EmparejamientoCalculado(
            id, compradorId.valor(), ofertasInmutables.size(), fechaCalculo));

    return resultado;
  }

  public static ResultadoEmparejamiento reconstituir(
      UUID id,
      CompradorId compradorId,
      CoordenadaGPS posicionOrigen,
      PreferenciaFiltro filtroAplicado,
      List<PuntajeOferta> ofertasOrdenadas,
      Instant fechaCalculo) {
    return new ResultadoEmparejamiento(
        id,
        compradorId,
        posicionOrigen,
        filtroAplicado,
        List.copyOf(ofertasOrdenadas),
        fechaCalculo);
  }

  private static void validarPosicionDentroDeQuito(CoordenadaGPS posicion) {
    boolean latitudValida =
        posicion.latitud() >= LATITUD_MINIMA_QUITO && posicion.latitud() <= LATITUD_MAXIMA_QUITO;
    boolean longitudValida =
        posicion.longitud() >= LONGITUD_MINIMA_QUITO
            && posicion.longitud() <= LONGITUD_MAXIMA_QUITO;
    if (!latitudValida || !longitudValida) {
      throw new PosicionFueraDeRangoException(
          "E-5: La posicion del comprador debe estar dentro de los limites geograficos de "
              + "Quito (lat ["
              + LATITUD_MINIMA_QUITO
              + ", "
              + LATITUD_MAXIMA_QUITO
              + "], lon ["
              + LONGITUD_MINIMA_QUITO
              + ", "
              + LONGITUD_MAXIMA_QUITO
              + "]). Recibido: "
              + posicion);
    }
  }

  private void registrar(EventoDominio evento) {
    eventos.add(evento);
  }

  public List<EventoDominio> eventos() {
    return Collections.unmodifiableList(eventos);
  }

  public void limpiarEventos() {
    eventos.clear();
  }

  public UUID id() {
    return id;
  }

  public CompradorId compradorId() {
    return compradorId;
  }

  public CoordenadaGPS posicionOrigen() {
    return posicionOrigen;
  }

  public PreferenciaFiltro filtroAplicado() {
    return filtroAplicado;
  }

  public List<PuntajeOferta> ofertasOrdenadas() {
    return ofertasOrdenadas;
  }

  public Instant fechaCalculo() {
    return fechaCalculo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ResultadoEmparejamiento otro)) {
      return false;
    }
    return id.equals(otro.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
