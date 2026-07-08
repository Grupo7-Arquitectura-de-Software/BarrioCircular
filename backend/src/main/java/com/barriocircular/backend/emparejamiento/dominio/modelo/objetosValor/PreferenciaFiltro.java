package com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor;

import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.FiltroInvalidoException;
import java.util.Set;

public record PreferenciaFiltro(
    Set<TipoMaterialFiltro> tiposMaterial, double radioMaximoKm, String zonaDescriptiva) {
  private static final double RADIO_MAXIMO_KM = 50;

  public PreferenciaFiltro {
    if (tiposMaterial == null || tiposMaterial.isEmpty()) {
      throw new FiltroInvalidoException("Debe seleccionar al menos un tipo de material.");
    }

    if (tiposMaterial.stream().anyMatch(java.util.Objects::isNull)) {
      throw new FiltroInvalidoException("Los tipos de material no pueden contener valores nulos.");
    }

    if (!Double.isFinite(radioMaximoKm) || radioMaximoKm <= 0 || radioMaximoKm > RADIO_MAXIMO_KM) {
      throw new FiltroInvalidoException(
          "El radio maximo (km) debe ser mayor que 0 y menor o igual a 50. Recibido:",
          radioMaximoKm);
    }

    tiposMaterial = Set.copyOf(tiposMaterial);
  }
}
