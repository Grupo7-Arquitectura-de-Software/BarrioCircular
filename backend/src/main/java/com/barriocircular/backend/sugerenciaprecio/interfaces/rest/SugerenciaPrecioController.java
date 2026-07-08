package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso.SugerirPrecioUseCase;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.SugerirPrecioCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaPrecioResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.sugerenciaprecio.interfaces.rest.dto.SugerirPrecioRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sugerencias-precio")
public class SugerenciaPrecioController {

  private final SugerirPrecioUseCase sugerirPrecioUseCase;

  public SugerenciaPrecioController(SugerirPrecioUseCase sugerirPrecioUseCase) {
    this.sugerirPrecioUseCase = sugerirPrecioUseCase;
  }

  @PostMapping
  public ResponseEntity<SugerenciaPrecioResultado> sugerirPrecio(
      @RequestBody SugerirPrecioRequest solicitud, Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    SugerirPrecioCommand comando =
        new SugerirPrecioCommand(
            solicitud.tipoResiduo(), solicitud.pesoKg(), solicitud.imagenBase64());
    SugerenciaPrecioResultado resultado = sugerirPrecioUseCase.ejecutar(comando, clerkId);
    return ResponseEntity.ok(resultado);
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
