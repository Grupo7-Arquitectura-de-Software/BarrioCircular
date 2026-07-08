package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = SugerenciaPrecioController.class)
public class SugerenciaPrecioExceptionHandler {

  @ExceptionHandler(IdentidadAutenticadaNoDisponibleException.class)
  ProblemDetail manejarNoAutenticado(IdentidadAutenticadaNoDisponibleException excepcion) {
    return crearProblema(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
  }

  @ExceptionHandler({TipoMaterialSugeridoInvalidoException.class, IllegalArgumentException.class})
  ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
    return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
  }

  private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
    problema.setTitle(estadoHttp.getReasonPhrase());
    return problema;
  }
}
