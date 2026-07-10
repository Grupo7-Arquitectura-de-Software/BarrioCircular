package com.barriocircular.backend.logistica.dominio.objetosValor;

public record TiempoEstimadoLlegada(long minutosDesdeInicioRuta) {

  public TiempoEstimadoLlegada {
    if (minutosDesdeInicioRuta < 0) {
      throw new IllegalArgumentException("El tiempo estimado de llegada no puede ser negativo.");
    }
  }

  public static TiempoEstimadoLlegada deMinutos(long minutosDesdeInicioRuta) {
    return new TiempoEstimadoLlegada(minutosDesdeInicioRuta);
  }
}
