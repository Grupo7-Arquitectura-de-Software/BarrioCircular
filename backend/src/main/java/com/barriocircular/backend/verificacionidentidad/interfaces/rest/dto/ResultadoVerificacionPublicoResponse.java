package com.barriocircular.backend.verificacionidentidad.interfaces.rest.dto;

import com.barriocircular.backend.verificacionidentidad.aplicacion.dto.ResultadoVerificacionPublico;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResultadoVerificacionPublicoResponse(
    boolean valido,
    String nombreMostrado,
    String rol,
    Instant fechaEmision,
    Long antiguedadEnPlataformaDias) {

  public static ResultadoVerificacionPublicoResponse desde(ResultadoVerificacionPublico resultado) {
    return new ResultadoVerificacionPublicoResponse(
        resultado.valido(),
        resultado.nombreMostrado(),
        resultado.rol(),
        resultado.fechaEmision(),
        resultado.antiguedadEnPlataformaDias());
  }
}
