package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso.AnalizarMaterialUseCase;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.AnalizarMaterialCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisMaterialResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.sugerenciaprecio.interfaces.rest.dto.AnalizarMaterialRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analisis-material")
public class AnalisisMaterialController {

  private final AnalizarMaterialUseCase analizarMaterialUseCase;

  public AnalisisMaterialController(AnalizarMaterialUseCase analizarMaterialUseCase) {
    this.analizarMaterialUseCase = analizarMaterialUseCase;
  }

  @PostMapping
  public ResponseEntity<AnalisisMaterialResultado> analizarMaterial(
      @RequestBody AnalizarMaterialRequest solicitud, Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    AnalizarMaterialCommand comando = new AnalizarMaterialCommand(solicitud.imagenBase64());
    AnalisisMaterialResultado resultado = analizarMaterialUseCase.ejecutar(comando, clerkId);
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
