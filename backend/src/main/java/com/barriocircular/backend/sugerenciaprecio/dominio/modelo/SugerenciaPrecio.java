package com.barriocircular.backend.sugerenciaprecio.dominio.modelo;

import java.time.Instant;
import java.util.Objects;

public class SugerenciaPrecio {

  private final SugerenciaPrecioId id;
  private final TipoMaterialSugerido tipoMaterial;
  private final Double pesoKg;
  private final PrecioSugerido precioSugerido;
  private final FuenteSugerencia fuente;
  private final String justificacion;
  private final String solicitanteClerkId;
  private final Instant fechaSugerencia;

  private SugerenciaPrecio(
      SugerenciaPrecioId id,
      TipoMaterialSugerido tipoMaterial,
      Double pesoKg,
      PrecioSugerido precioSugerido,
      FuenteSugerencia fuente,
      String justificacion,
      String solicitanteClerkId,
      Instant fechaSugerencia) {
    this.id = id;
    this.tipoMaterial = tipoMaterial;
    this.pesoKg = pesoKg;
    this.precioSugerido = precioSugerido;
    this.fuente = fuente;
    this.justificacion = justificacion;
    this.solicitanteClerkId = solicitanteClerkId;
    this.fechaSugerencia = fechaSugerencia;
  }

  public static SugerenciaPrecio generar(
      TipoMaterialSugerido tipoMaterial,
      Double pesoKg,
      PrecioSugerido precioSugerido,
      FuenteSugerencia fuente,
      String justificacion,
      String solicitanteClerkId) {
    Objects.requireNonNull(tipoMaterial, "El tipo de material es obligatorio.");
    Objects.requireNonNull(precioSugerido, "El precio sugerido es obligatorio.");
    Objects.requireNonNull(fuente, "La fuente de la sugerencia es obligatoria.");

    return new SugerenciaPrecio(
        SugerenciaPrecioId.nuevo(),
        tipoMaterial,
        pesoKg,
        precioSugerido,
        fuente,
        justificacion,
        solicitanteClerkId,
        Instant.now());
  }

  public static SugerenciaPrecio reconstituir(
      SugerenciaPrecioId id,
      TipoMaterialSugerido tipoMaterial,
      Double pesoKg,
      PrecioSugerido precioSugerido,
      FuenteSugerencia fuente,
      String justificacion,
      String solicitanteClerkId,
      Instant fechaSugerencia) {
    return new SugerenciaPrecio(
        id,
        tipoMaterial,
        pesoKg,
        precioSugerido,
        fuente,
        justificacion,
        solicitanteClerkId,
        fechaSugerencia);
  }

  public SugerenciaPrecioId id() {
    return id;
  }

  public TipoMaterialSugerido tipoMaterial() {
    return tipoMaterial;
  }

  public Double pesoKg() {
    return pesoKg;
  }

  public PrecioSugerido precioSugerido() {
    return precioSugerido;
  }

  public FuenteSugerencia fuente() {
    return fuente;
  }

  public String justificacion() {
    return justificacion;
  }

  public String solicitanteClerkId() {
    return solicitanteClerkId;
  }

  public Instant fechaSugerencia() {
    return fechaSugerencia;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SugerenciaPrecio otra)) {
      return false;
    }
    return id.equals(otra.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
