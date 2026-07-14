package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import java.time.Instant;
import java.util.Objects;

/**
 * Resultado persistido de analizar la foto de un material con IA. Invariante central: solo un
 * análisis {@link ResultadoAnalisis#VALIDO} lleva sugerencias (tipo, estado y precio obligatorios;
 * peso opcional si la IA no pudo estimarlo); cualquier otro resultado las deja todas en nulo.
 */
public class AnalisisMaterial {

  private final AnalisisMaterialId id;
  private final ResultadoAnalisis resultado;
  private final TipoMaterialSugerido tipoMaterial;
  private final Double pesoEstimadoKg;
  private final EstadoMaterial estadoMaterial;
  private final PrecioSugerido precioSugerido;
  private final String recomendacion;
  private final String solicitanteClerkId;
  private final Instant fechaAnalisis;

  private AnalisisMaterial(
      AnalisisMaterialId id,
      ResultadoAnalisis resultado,
      TipoMaterialSugerido tipoMaterial,
      Double pesoEstimadoKg,
      EstadoMaterial estadoMaterial,
      PrecioSugerido precioSugerido,
      String recomendacion,
      String solicitanteClerkId,
      Instant fechaAnalisis) {
    this.id = id;
    this.resultado = resultado;
    this.tipoMaterial = tipoMaterial;
    this.pesoEstimadoKg = pesoEstimadoKg;
    this.estadoMaterial = estadoMaterial;
    this.precioSugerido = precioSugerido;
    this.recomendacion = recomendacion;
    this.solicitanteClerkId = solicitanteClerkId;
    this.fechaAnalisis = fechaAnalisis;
  }

  public static AnalisisMaterial generar(
      ResultadoAnalisis resultado,
      TipoMaterialSugerido tipoMaterial,
      Double pesoEstimadoKg,
      EstadoMaterial estadoMaterial,
      PrecioSugerido precioSugerido,
      String recomendacion,
      String solicitanteClerkId) {
    Objects.requireNonNull(resultado, "El resultado del análisis es obligatorio.");

    if (resultado == ResultadoAnalisis.VALIDO) {
      Objects.requireNonNull(tipoMaterial, "Un análisis válido requiere el tipo de material.");
      Objects.requireNonNull(estadoMaterial, "Un análisis válido requiere el estado del material.");
      Objects.requireNonNull(precioSugerido, "Un análisis válido requiere el precio sugerido.");
    } else if (tipoMaterial != null
        || pesoEstimadoKg != null
        || estadoMaterial != null
        || precioSugerido != null) {
      throw new IllegalArgumentException(
          "Un análisis " + resultado + " no puede llevar sugerencias de material o precio.");
    }

    return new AnalisisMaterial(
        AnalisisMaterialId.nuevo(),
        resultado,
        tipoMaterial,
        pesoEstimadoKg,
        estadoMaterial,
        precioSugerido,
        recomendacion,
        solicitanteClerkId,
        Instant.now());
  }

  public static AnalisisMaterial reconstituir(
      AnalisisMaterialId id,
      ResultadoAnalisis resultado,
      TipoMaterialSugerido tipoMaterial,
      Double pesoEstimadoKg,
      EstadoMaterial estadoMaterial,
      PrecioSugerido precioSugerido,
      String recomendacion,
      String solicitanteClerkId,
      Instant fechaAnalisis) {
    return new AnalisisMaterial(
        id,
        resultado,
        tipoMaterial,
        pesoEstimadoKg,
        estadoMaterial,
        precioSugerido,
        recomendacion,
        solicitanteClerkId,
        fechaAnalisis);
  }

  public AnalisisMaterialId id() {
    return id;
  }

  public ResultadoAnalisis resultado() {
    return resultado;
  }

  public TipoMaterialSugerido tipoMaterial() {
    return tipoMaterial;
  }

  public Double pesoEstimadoKg() {
    return pesoEstimadoKg;
  }

  public EstadoMaterial estadoMaterial() {
    return estadoMaterial;
  }

  public PrecioSugerido precioSugerido() {
    return precioSugerido;
  }

  public String recomendacion() {
    return recomendacion;
  }

  public String solicitanteClerkId() {
    return solicitanteClerkId;
  }

  public Instant fechaAnalisis() {
    return fechaAnalisis;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AnalisisMaterial otro)) {
      return false;
    }
    return id.equals(otro.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
