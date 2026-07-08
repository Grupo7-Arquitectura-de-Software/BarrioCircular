package com.barriocircular.backend.verificacionidentidad.aplicacion.dto;

import java.time.Instant;

public record ResultadoVerificacionPublico(
    boolean valido,
    String nombreMostrado,
    String rol,
    Instant fechaEmision,
    Long antiguedadEnPlataformaDias) {

  public static ResultadoVerificacionPublico invalido() {
    return new ResultadoVerificacionPublico(false, null, null, null, null);
  }

  public static ResultadoVerificacionPublico valido(
      String nombreMostrado, String rol, Instant fechaEmision, Long antiguedadEnPlataformaDias) {
    return new ResultadoVerificacionPublico(
        true, nombreMostrado, rol, fechaEmision, antiguedadEnPlataformaDias);
  }
}
