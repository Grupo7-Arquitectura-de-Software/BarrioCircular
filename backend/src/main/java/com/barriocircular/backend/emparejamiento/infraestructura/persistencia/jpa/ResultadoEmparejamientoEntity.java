package com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "resultados_emparejamiento")
public class ResultadoEmparejamientoEntity {

  @Id private UUID id;

  @Column(name = "comprador_id", nullable = false)
  private UUID compradorId;

  @Column(name = "latitud_origen", nullable = false)
  private double latitudOrigen;

  @Column(name = "longitud_origen", nullable = false)
  private double longitudOrigen;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "emparejamiento_filtro_tipos",
      joinColumns = @JoinColumn(name = "resultado_id"))
  @Column(name = "tipo_material", length = 40)
  private Set<String> tiposMaterialFiltro;

  @Column(name = "radio_maximo_km", nullable = false)
  private double radioMaximoKm;

  @Column(name = "zona_descriptiva", length = 120)
  private String zonaDescriptiva;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "emparejamiento_ofertas_ordenadas",
      joinColumns = @JoinColumn(name = "resultado_id"))
  @OrderColumn(name = "posicion")
  private List<PuntajeOfertaEmbeddable> ofertasOrdenadas;

  @Column(name = "fecha_calculo", nullable = false)
  private Instant fechaCalculo;

  protected ResultadoEmparejamientoEntity() {}

  public ResultadoEmparejamientoEntity(
      UUID id,
      UUID compradorId,
      double latitudOrigen,
      double longitudOrigen,
      Set<String> tiposMaterialFiltro,
      double radioMaximoKm,
      String zonaDescriptiva,
      List<PuntajeOfertaEmbeddable> ofertasOrdenadas,
      Instant fechaCalculo) {
    this.id = id;
    this.compradorId = compradorId;
    this.latitudOrigen = latitudOrigen;
    this.longitudOrigen = longitudOrigen;
    this.tiposMaterialFiltro = tiposMaterialFiltro;
    this.radioMaximoKm = radioMaximoKm;
    this.zonaDescriptiva = zonaDescriptiva;
    this.ofertasOrdenadas = ofertasOrdenadas;
    this.fechaCalculo = fechaCalculo;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCompradorId() {
    return compradorId;
  }

  public double getLatitudOrigen() {
    return latitudOrigen;
  }

  public double getLongitudOrigen() {
    return longitudOrigen;
  }

  public Set<String> getTiposMaterialFiltro() {
    return tiposMaterialFiltro;
  }

  public double getRadioMaximoKm() {
    return radioMaximoKm;
  }

  public String getZonaDescriptiva() {
    return zonaDescriptiva;
  }

  public List<PuntajeOfertaEmbeddable> getOfertasOrdenadas() {
    return ofertasOrdenadas;
  }

  public Instant getFechaCalculo() {
    return fechaCalculo;
  }
}
