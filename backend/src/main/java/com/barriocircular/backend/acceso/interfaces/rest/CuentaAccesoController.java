package com.barriocircular.backend.acceso.interfaces.rest;

import com.barriocircular.backend.acceso.aplicacion.casosdeuso.RegistrarCuentaCasoUso;
import com.barriocircular.backend.acceso.aplicacion.comandos.RegistrarCuentaCommand;
import com.barriocircular.backend.acceso.aplicacion.dto.RegistrarCuentaRespuesta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acceso")
public class CuentaAccesoController {

  private final RegistrarCuentaCasoUso registrarCuentaCasoUso;

  public CuentaAccesoController(RegistrarCuentaCasoUso registrarCuentaCasoUso) {
    this.registrarCuentaCasoUso = registrarCuentaCasoUso;
  }

  @PostMapping("/sesion")
  public ResponseEntity<RegistrarCuentaRespuesta> iniciarSesion(
      @RequestBody RegistrarCuentaCommand comando) {
    RegistrarCuentaRespuesta respuesta = registrarCuentaCasoUso.ejecutar(comando);
    return ResponseEntity.ok(respuesta);
  }
}
