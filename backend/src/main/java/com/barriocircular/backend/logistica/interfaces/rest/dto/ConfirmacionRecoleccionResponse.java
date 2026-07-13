package com.barriocircular.backend.logistica.interfaces.rest.dto;

import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionRecoleccionResultado;
import java.util.UUID;

public record ConfirmacionRecoleccionResponse(
    UUID rutaId,
    String estadoRuta,
    UUID paradaId,
    String estadoParada,
    UUID publicacionId,
    String estadoPublicacion,
    Double pesoRealVerificado,
    boolean rutaTerminada) {

  public static ConfirmacionRecoleccionResponse desde(ConfirmacionRecoleccionResultado resultado) {
    return new ConfirmacionRecoleccionResponse(
        resultado.rutaId(),
        resultado.estadoRuta(),
        resultado.paradaId(),
        resultado.estadoParada(),
        resultado.publicacionId(),
        resultado.estadoPublicacion(),
        resultado.pesoRealVerificado(),
        resultado.rutaTerminada());
  }
}
