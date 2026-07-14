package com.barriocircular.backend.logistica.interfaces.rest;

import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ActualizarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ConfirmarRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ConstruirRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.FinalizarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.IniciarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ObtenerRutaActivaUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ObtenerRutaPorIdUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.RegistrarLlegadaParadaUseCase;
import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.interfaces.rest.dto.ConfirmacionRecoleccionResponse;
import com.barriocircular.backend.logistica.interfaces.rest.dto.ConfirmarRecoleccionRequest;
import com.barriocircular.backend.logistica.interfaces.rest.dto.ConstruirRutaRequest;
import com.barriocircular.backend.logistica.interfaces.rest.dto.RegistrarLlegadaParadaRequest;
import com.barriocircular.backend.logistica.interfaces.rest.dto.RutaRecoleccionResponse;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/logistica/rutas")
public class LogisticaRecoleccionController {

  private static final String ROL_RECICLADOR = "RECICLADOR";
  private static final String ESTADO_ACTIVO = "ACTIVO";

  private final ConstruirRutaRecoleccionUseCase construirRutaRecoleccionUseCase;
  private final ObtenerRutaActivaUseCase obtenerRutaActivaUseCase;
  private final ActualizarRutaRecoleccionUseCase actualizarRutaRecoleccionUseCase;
  private final ObtenerRutaPorIdUseCase obtenerRutaPorIdUseCase;
  private final RegistrarLlegadaParadaUseCase registrarLlegadaParadaUseCase;
  private final ConfirmarRecoleccionUseCase confirmarRecoleccionUseCase;
  private final IniciarRutaRecoleccionUseCase iniciarRutaRecoleccionUseCase;
  private final FinalizarRutaRecoleccionUseCase finalizarRutaRecoleccionUseCase;
  private final ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase;

  public LogisticaRecoleccionController(
      ConstruirRutaRecoleccionUseCase construirRutaRecoleccionUseCase,
      ObtenerRutaActivaUseCase obtenerRutaActivaUseCase,
      ActualizarRutaRecoleccionUseCase actualizarRutaRecoleccionUseCase,
      ObtenerRutaPorIdUseCase obtenerRutaPorIdUseCase,
      RegistrarLlegadaParadaUseCase registrarLlegadaParadaUseCase,
      ConfirmarRecoleccionUseCase confirmarRecoleccionUseCase,
      IniciarRutaRecoleccionUseCase iniciarRutaRecoleccionUseCase,
      FinalizarRutaRecoleccionUseCase finalizarRutaRecoleccionUseCase,
      ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase) {
    this.construirRutaRecoleccionUseCase = Objects.requireNonNull(construirRutaRecoleccionUseCase);
    this.obtenerRutaActivaUseCase = Objects.requireNonNull(obtenerRutaActivaUseCase);
    this.actualizarRutaRecoleccionUseCase =
        Objects.requireNonNull(actualizarRutaRecoleccionUseCase);
    this.obtenerRutaPorIdUseCase = Objects.requireNonNull(obtenerRutaPorIdUseCase);
    this.registrarLlegadaParadaUseCase = Objects.requireNonNull(registrarLlegadaParadaUseCase);
    this.confirmarRecoleccionUseCase = Objects.requireNonNull(confirmarRecoleccionUseCase);
    this.iniciarRutaRecoleccionUseCase = Objects.requireNonNull(iniciarRutaRecoleccionUseCase);
    this.finalizarRutaRecoleccionUseCase = Objects.requireNonNull(finalizarRutaRecoleccionUseCase);
    this.obtenerPerfilPorClerkIdUseCase = Objects.requireNonNull(obtenerPerfilPorClerkIdUseCase);
  }

  @PostMapping("/activa/iniciar")
  public ResponseEntity<RutaRecoleccionResponse> iniciarRutaActiva(Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    RutaRecoleccionResultado resultado = iniciarRutaRecoleccionUseCase.ejecutar(recicladorId);
    return ResponseEntity.ok(RutaRecoleccionResponse.desde(resultado));
  }

  @PostMapping("/activa/finalizar")
  public ResponseEntity<RutaRecoleccionResponse> finalizarRutaActiva(Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    RutaRecoleccionResultado resultado = finalizarRutaRecoleccionUseCase.ejecutar(recicladorId);
    return ResponseEntity.ok(RutaRecoleccionResponse.desde(resultado));
  }

  @PostMapping
  public ResponseEntity<RutaRecoleccionResponse> construirRuta(
      @RequestBody ConstruirRutaRequest solicitud, Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    RutaRecoleccionResultado resultado =
        construirRutaRecoleccionUseCase.ejecutar(
            recicladorId, solicitud.horaInicioRuta(), solicitud.fechaRuta());
    return ResponseEntity.status(HttpStatus.CREATED).body(RutaRecoleccionResponse.desde(resultado));
  }

  @GetMapping("/activa")
  public ResponseEntity<RutaRecoleccionResponse> obtenerRutaActiva(Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    return obtenerRutaActivaUseCase
        .ejecutar(recicladorId)
        .map(RutaRecoleccionResponse::desde)
        .map(ResponseEntity::ok)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta activa no encontrada."));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RutaRecoleccionResponse> obtenerRuta(
      @PathVariable UUID id, Authentication autenticacion) {
    obtenerRecicladorAutenticado(autenticacion);
    return obtenerRutaPorIdUseCase
        .ejecutar(id)
        .map(RutaRecoleccionResponse::desde)
        .map(ResponseEntity::ok)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ruta no encontrada."));
  }

  @PutMapping("/activa")
  public ResponseEntity<RutaRecoleccionResponse> actualizarRutaActiva(
      Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    RutaRecoleccionResultado resultado = actualizarRutaRecoleccionUseCase.ejecutar(recicladorId);
    return ResponseEntity.ok(RutaRecoleccionResponse.desde(resultado));
  }

  @PatchMapping("/{rutaId}/paradas/{paradaId}/llegada")
  public ResponseEntity<RutaRecoleccionResponse> registrarLlegada(
      @PathVariable UUID rutaId,
      @PathVariable UUID paradaId,
      @RequestBody RegistrarLlegadaParadaRequest solicitud,
      Authentication autenticacion) {
    obtenerRecicladorAutenticado(autenticacion);
    RutaRecoleccionResultado resultado =
        registrarLlegadaParadaUseCase.ejecutar(
            rutaId, paradaId, solicitud.fechaLlegada(), solicitud.horaLlegada());
    return ResponseEntity.ok(RutaRecoleccionResponse.desde(resultado));
  }

  @PostMapping("/{rutaId}/paradas/{paradaId}/confirmar")
  public ResponseEntity<ConfirmacionRecoleccionResponse> confirmarRecoleccion(
      @PathVariable UUID rutaId,
      @PathVariable UUID paradaId,
      @RequestBody ConfirmarRecoleccionRequest solicitud,
      Authentication autenticacion) {
    UUID recicladorId = obtenerRecicladorAutenticado(autenticacion);
    ConfirmacionRecoleccionResultado resultado =
        confirmarRecoleccionUseCase.ejecutar(
            recicladorId,
            rutaId,
            paradaId,
            solicitud.pesoRealVerificado(),
            solicitud.observaciones());
    return ResponseEntity.ok(ConfirmacionRecoleccionResponse.desde(resultado));
  }

  private UUID obtenerRecicladorAutenticado(Authentication autenticacion) {
    PerfilResultado perfil = obtenerPerfilPorClerkIdUseCase.ejecutar(extraerClerkId(autenticacion));
    if (!ROL_RECICLADOR.equals(perfil.rol()) || !ESTADO_ACTIVO.equals(perfil.estadoPerfil())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Solo un reciclador activo puede gestionar rutas.");
    }
    return perfil.perfilId();
  }

  private String extraerClerkId(Authentication autenticacion) {
    if (autenticacion == null
        || !(autenticacion.getPrincipal() instanceof Jwt jwt)
        || jwt.getSubject() == null
        || jwt.getSubject().isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identidad no disponible.");
    }
    return jwt.getSubject();
  }
}
