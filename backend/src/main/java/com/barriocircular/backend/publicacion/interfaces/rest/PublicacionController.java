package com.barriocircular.backend.publicacion.interfaces.rest;

import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.CrearPublicacionUseCase;
import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.ReservarPublicacionUseCase;
import com.barriocircular.backend.publicacion.aplicacion.comandos.CrearPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.comandos.ReservarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.publicacion.interfaces.rest.dto.CrearPublicacionRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

  private final CrearPublicacionUseCase crearPublicacionUseCase;
  private final ReservarPublicacionUseCase reservarPublicacionUseCase;

  public PublicacionController(
      CrearPublicacionUseCase crearPublicacionUseCase,
      ReservarPublicacionUseCase reservarPublicacionUseCase) {
    this.crearPublicacionUseCase = crearPublicacionUseCase;
    this.reservarPublicacionUseCase = reservarPublicacionUseCase;
  }

  @PostMapping
  public ResponseEntity<PublicacionResultado> crearPublicacion(
      @RequestBody CrearPublicacionRequest solicitud, Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    CrearPublicacionCommand comando = crearComando(solicitud);
    PublicacionResultado resultado = crearPublicacionUseCase.ejecutar(comando, clerkId);
    return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
  }

  @PostMapping("/{publicacionId}/reservar")
  public ResponseEntity<PublicacionResultado> reservarPublicacion(
      @PathVariable UUID publicacionId, Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    ReservarPublicacionCommand comando = new ReservarPublicacionCommand(publicacionId);
    PublicacionResultado resultado = reservarPublicacionUseCase.ejecutar(comando, clerkId);
    return ResponseEntity.ok(resultado);
  }

  private CrearPublicacionCommand crearComando(CrearPublicacionRequest solicitud) {
    return new CrearPublicacionCommand(
        solicitud.tipoResiduo(),
        solicitud.pesoKg(),
        solicitud.precioPorKilo(),
        solicitud.latitud(),
        solicitud.longitud(),
        solicitud.evidenciaUrl());
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
