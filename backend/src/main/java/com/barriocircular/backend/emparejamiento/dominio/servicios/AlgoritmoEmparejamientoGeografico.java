package com.barriocircular.backend.emparejamiento.dominio.servicios;

import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PreferenciaFiltro;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PuntajeOferta;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AlgoritmoEmparejamientoGeografico {

  private static final String ESTADO_DISPONIBLE = "DISPONIBLE";

  private final CalculadorDistanciaGeografica calculadorDistancia;

  public AlgoritmoEmparejamientoGeografico(CalculadorDistanciaGeografica calculadorDistancia) {
    this.calculadorDistancia =
        Objects.requireNonNull(calculadorDistancia, "El calculador de distancia es obligatorio.");
  }

  public List<PuntajeOferta> calcularOfertasOptimas(
      CoordenadaGPS posicionComprador,
      PreferenciaFiltro filtro,
      List<OfertaCatalogo> catalogoDisponible) {
    Objects.requireNonNull(posicionComprador, "La posicion del comprador es obligatoria.");
    Objects.requireNonNull(filtro, "El filtro de busqueda es obligatorio.");
    Objects.requireNonNull(catalogoDisponible, "El catalogo de publicaciones no puede ser nulo.");

    return catalogoDisponible.stream()
        .filter(oferta -> ESTADO_DISPONIBLE.equals(oferta.estado()))
        .filter(oferta -> filtro.tiposMaterial().contains(oferta.tipoResiduo()))
        .map(oferta -> puntuarOferta(posicionComprador, filtro, oferta))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparingDouble(PuntajeOferta::scoreTotal).reversed())
        .toList();
  }

  private Optional<PuntajeOferta> puntuarOferta(
      CoordenadaGPS posicionComprador, PreferenciaFiltro filtro, OfertaCatalogo oferta) {
    CoordenadaGPS posicionOferta = new CoordenadaGPS(oferta.latitud(), oferta.longitud());
    double distanciaKm = calculadorDistancia.distanciaKm(posicionComprador, posicionOferta);

    if (distanciaKm > filtro.radioMaximoKm()) {
      return Optional.empty();
    }

    double distanciaEfectivaKm = Math.max(distanciaKm, PuntajeOferta.DISTANCIA_MINIMA_KM);
    double scoreTotal = oferta.precioPorKilo().doubleValue() / distanciaEfectivaKm;

    return Optional.of(
        new PuntajeOferta(
            oferta.publicacionId(), distanciaEfectivaKm, oferta.precioPorKilo(), scoreTotal));
  }
}
