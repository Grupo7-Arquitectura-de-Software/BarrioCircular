package com.barriocircular.backend.emparejamiento.interfaces.rest;

import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.CatalogoPublicacionesNoDisponibleException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.CoordenadaInvalidaException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.FiltroInvalidoException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.OfertaCatalogoInvalidaException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.PosicionFueraDeRangoException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.PuntajeInvalidoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = EmparejamientoController.class)
public class EmparejamientoExceptionHandler {

  @ExceptionHandler(PerfilNoEncontradoException.class)
  ProblemDetail manejarNoEncontrado(PerfilNoEncontradoException excepcion) {
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

  @ExceptionHandler({
    FiltroInvalidoException.class,
    CoordenadaInvalidaException.class,
    PosicionFueraDeRangoException.class,
    OfertaCatalogoInvalidaException.class,
    PuntajeInvalidoException.class,
    IllegalArgumentException.class
  })
  ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
    return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
  }

  @ExceptionHandler(CatalogoPublicacionesNoDisponibleException.class)
  ProblemDetail manejarIntegracionNoDisponible(
      CatalogoPublicacionesNoDisponibleException excepcion) {
    return crearProblema(HttpStatus.SERVICE_UNAVAILABLE, excepcion.getMessage());
  }

  private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
    problema.setTitle(estadoHttp.getReasonPhrase());
    return problema;
  }
}
