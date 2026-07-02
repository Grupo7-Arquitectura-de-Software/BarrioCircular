package com.barriocircular.backend.perfiles.interfaces.rest;

import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.CrearPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaUsuarioNoAutorizadaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.interfaces.rest.dto.CompletarPerfilRequest;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilUsuarioController {

  private final CrearPerfilUseCase crearPerfilUseCase;
  private final ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase;
  private final CuentaAccesoConsultor cuentaAccesoConsultor;

  public PerfilUsuarioController(
      CrearPerfilUseCase crearPerfilUseCase,
      ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase,
      CuentaAccesoConsultor cuentaAccesoConsultor) {
    this.crearPerfilUseCase = crearPerfilUseCase;
    this.obtenerPerfilPorClerkIdUseCase = obtenerPerfilPorClerkIdUseCase;
    this.cuentaAccesoConsultor = cuentaAccesoConsultor;
  }

  @GetMapping("/me")
  public ResponseEntity<PerfilResultado> obtenerMiPerfil(Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    return ResponseEntity.ok(obtenerPerfilPorClerkIdUseCase.ejecutar(clerkId));
  }

  @PostMapping("/completar")
  public ResponseEntity<PerfilResultado> completarPerfil(
      @RequestBody CompletarPerfilRequest solicitud, Authentication autenticacion) {
    UUID cuentaUsuarioAutenticadaId = obtenerCuentaUsuarioAutenticadaId(autenticacion);
    validarPertenenciaCuentaSolicitada(solicitud.cuentaUsuarioId(), cuentaUsuarioAutenticadaId);

    CrearPerfilCommand comandoCrearPerfil = crearComando(solicitud, cuentaUsuarioAutenticadaId);
    PerfilResultado perfilCreado = crearPerfilUseCase.ejecutar(comandoCrearPerfil);
    return ResponseEntity.ok(perfilCreado);
  }

  private UUID obtenerCuentaUsuarioAutenticadaId(Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    return cuentaAccesoConsultor
        .obtenerCuentaIdPorClerkId(clerkId)
        .orElseThrow(CuentaAccesoNoEncontradaException::new);
  }

  private void validarPertenenciaCuentaSolicitada(
      UUID cuentaUsuarioSolicitadaId, UUID cuentaUsuarioAutenticadaId) {
    if (cuentaUsuarioSolicitadaId != null
        && !cuentaUsuarioSolicitadaId.equals(cuentaUsuarioAutenticadaId)) {
      throw new CuentaUsuarioNoAutorizadaException();
    }
  }

  private CrearPerfilCommand crearComando(
      CompletarPerfilRequest solicitud, UUID cuentaUsuarioAutenticadaId) {
    return new CrearPerfilCommand(
        cuentaUsuarioAutenticadaId,
        solicitud.documentoIdentificacion(),
        solicitud.nombreCompleto(),
        solicitud.nombreComercial(),
        solicitud.rol(),
        solicitud.correoElectronico(),
        solicitud.telefono(),
        solicitud.latitud(),
        solicitud.longitud());
  }

  private String extraerClerkId(Authentication autenticacion) {
    if (autenticacion == null
        || !(autenticacion.getPrincipal() instanceof Jwt jwt)
        || jwt.getSubject() == null
        || jwt.getSubject().isBlank()) {
      throw new IdentidadAutenticadaNoDisponibleException();
    }
    return jwt.getSubject();
  }
}
