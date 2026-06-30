package com.barriocircular.backend.perfiles.interfaces.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaUsuarioNoAutorizadaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilYaExisteException;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;

@RestControllerAdvice(assignableTypes = PerfilUsuarioController.class)
public class PerfilUsuarioExceptionHandler {

    @ExceptionHandler({ CuentaAccesoNoEncontradaException.class, PerfilNoEncontradoException.class })
    ProblemDetail manejarNoEncontrado(RuntimeException excepcion) {
        return crearProblema(HttpStatus.NOT_FOUND, excepcion.getMessage());
    }

    @ExceptionHandler({ PerfilYaExisteException.class, DataIntegrityViolationException.class })
    ProblemDetail manejarConflicto(RuntimeException excepcion) {
        return crearProblema(HttpStatus.CONFLICT,
                excepcion instanceof PerfilYaExisteException
                        ? excepcion.getMessage()
                        : "El perfil no pudo registrarse porque ya existe información asociada.");
    }

    @ExceptionHandler(CuentaUsuarioNoAutorizadaException.class)
    ProblemDetail manejarProhibido(CuentaUsuarioNoAutorizadaException excepcion) {
        return crearProblema(HttpStatus.FORBIDDEN, excepcion.getMessage());
    }

    @ExceptionHandler(IdentidadAutenticadaNoDisponibleException.class)
    ProblemDetail manejarNoAutenticado(IdentidadAutenticadaNoDisponibleException excepcion) {
        return crearProblema(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
    }

    @ExceptionHandler({ PerfilDomainException.class, IllegalArgumentException.class })
    ProblemDetail manejarSolicitudInvalida(RuntimeException excepcion) {
        return crearProblema(HttpStatus.BAD_REQUEST, excepcion.getMessage());
    }

    private ProblemDetail crearProblema(HttpStatus estadoHttp, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estadoHttp, detalle);
        problema.setTitle(estadoHttp.getReasonPhrase());
        return problema;
    }
}
