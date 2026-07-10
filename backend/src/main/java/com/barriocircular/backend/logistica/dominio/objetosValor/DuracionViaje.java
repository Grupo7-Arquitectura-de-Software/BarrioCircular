package com.barriocircular.backend.logistica.dominio.objetosValor;

import java.util.Objects;

public record DuracionViaje(long minutos) {

  public DuracionViaje {
    if (minutos < 0) {
      throw new IllegalArgumentException("La duracion del viaje no puede ser negativa.");
    }
  }

  public static DuracionViaje deMinutos(long minutos) {
    return new DuracionViaje(minutos);
  }

  public DuracionViaje sumar(DuracionViaje otra) {
    Objects.requireNonNull(otra, "La duracion a sumar es obligatoria.");
    return new DuracionViaje(minutos + otra.minutos());
  }
}
