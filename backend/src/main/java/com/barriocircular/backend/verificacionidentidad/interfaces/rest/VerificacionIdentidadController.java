package com.barriocircular.backend.verificacionidentidad.interfaces.rest;

import com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso.EmitirCredencialUseCase;
import com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso.VerificarCredencialUseCase;
import com.barriocircular.backend.verificacionidentidad.aplicacion.dto.CredencialEmitidaResultado;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.verificacionidentidad.interfaces.rest.dto.ResultadoVerificacionPublicoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verificacion-identidad")
public class VerificacionIdentidadController {

  private final EmitirCredencialUseCase emitirCredencialUseCase;
  private final VerificarCredencialUseCase verificarCredencialUseCase;

  public VerificacionIdentidadController(
      EmitirCredencialUseCase emitirCredencialUseCase,
      VerificarCredencialUseCase verificarCredencialUseCase) {
    this.emitirCredencialUseCase = emitirCredencialUseCase;
    this.verificarCredencialUseCase = verificarCredencialUseCase;
  }

  @PostMapping("/credenciales")
  public ResponseEntity<CredencialEmitidaResultado> emitirCredencial(Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    return ResponseEntity.ok(emitirCredencialUseCase.ejecutar(clerkId));
  }

  @GetMapping("/publico/{token}")
  public ResponseEntity<ResultadoVerificacionPublicoResponse> verificarCredencial(
      @PathVariable String token) {
    return ResponseEntity.ok(
        ResultadoVerificacionPublicoResponse.desde(verificarCredencialUseCase.ejecutar(token)));
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
