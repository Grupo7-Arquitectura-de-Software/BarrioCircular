package com.barriocircular.backend.publicacion.interfaces.rest;

import com.barriocircular.backend.publicacion.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;
import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;
import com.barriocircular.backend.publicacion.dominio.excepciones.UbicacionFueraDeRangoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PublicacionController.class)
public class PublicacionExceptionHandler {

  @ExceptionHandler({PublicacionNoEncontradaException.class, PerfilNoEncontradoException.class})
  ProblemDetail manejarNoEncontrado(RuntimeException excepcion) {
    return crearProblema(HttpStatus.NOT_FOUND, excepcion.getMessage());
  }

  @ExceptionHandler(PerfilNoAutorizadoException.class)
  ProblemDetail manejarProhibido(PerfilNoAutorizadoException excepcion) {
    return crearProblema(HttpStatus.FORBIDDEN, excepcion.getMessage());
  }

  @ExceptionHandler(IdentidadAutenticadaNoDisponibleException.class)
  ProblemDetail manejarNoAutenticado(IdentidadAutenticadaNoDisponibleException excepcion) {
    return crearProblema(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
  }

  @ExceptionHandler(EstadoInvalidoException.class)
  ProblemDetail manejarConflictoDeEstado(EstadoInvalidoException excepcion) {
    return crearProblema(HttpStatus.CONFLICT, excepcion.getMessage());
  }

  @ExceptionHandler({
    PublicacionInvalidaException.class,
    UbicacionFueraDeRangoException.class,
    IllegalArgumentException.class
  })
  ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
    return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
  }

  private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
    problema.setTitle(estadoHttp.getReasonPhrase());
    return problema;
  }
}
