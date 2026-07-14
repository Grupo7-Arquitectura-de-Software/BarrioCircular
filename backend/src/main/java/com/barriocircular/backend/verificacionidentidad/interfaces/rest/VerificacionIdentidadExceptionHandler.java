package com.barriocircular.backend.verificacionidentidad.interfaces.rest;

import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.CuentaNoActivaException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.PerfilNoActivoException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.RolNoElegibleException;
import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.VerificacionIdentidadException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = VerificacionIdentidadController.class)
public class VerificacionIdentidadExceptionHandler {

  @ExceptionHandler(PerfilNoEncontradoException.class)
  ProblemDetail manejarNoEncontrado(PerfilNoEncontradoException excepcion) {
    return crearProblema(HttpStatus.NOT_FOUND, excepcion.getMessage());
  }

  @ExceptionHandler({
    CuentaNoActivaException.class,
    PerfilNoActivoException.class,
    RolNoElegibleException.class
  })
  ProblemDetail manejarProhibido(RuntimeException excepcion) {
    return crearProblema(HttpStatus.FORBIDDEN, excepcion.getMessage());
  }

  @ExceptionHandler(IdentidadAutenticadaNoDisponibleException.class)
  ProblemDetail manejarNoAutenticado(IdentidadAutenticadaNoDisponibleException excepcion) {
    return crearProblema(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ProblemDetail manejarConflicto(DataIntegrityViolationException excepcion) {
    return crearProblema(HttpStatus.CONFLICT, "No fue posible registrar la credencial.");
  }

  @ExceptionHandler({VerificacionIdentidadException.class, IllegalArgumentException.class})
  ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
    return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
  }

  private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
    problema.setTitle(estadoHttp.getReasonPhrase());
    return problema;
  }
}
