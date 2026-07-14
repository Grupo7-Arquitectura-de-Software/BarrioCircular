package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.ImagenAnalisisInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AnalisisMaterialController.class)
public class AnalisisMaterialExceptionHandler {

  @ExceptionHandler(IdentidadAutenticadaNoDisponibleException.class)
  ProblemDetail manejarNoAutenticado(IdentidadAutenticadaNoDisponibleException excepcion) {
    return crearProblema(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
  }

  @ExceptionHandler({ImagenAnalisisInvalidaException.class, IllegalArgumentException.class})
  ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
    return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
  }

  private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
    problema.setTitle(estadoHttp.getReasonPhrase());
    return problema;
  }
}
