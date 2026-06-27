package com.barriocircular.backend.perfiles.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.CrearPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.interfaces.rest.dto.CompletarPerfilRequest;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilUsuarioController {

    private final CrearPerfilUseCase crearPerfilUseCase;

    public PerfilUsuarioController(CrearPerfilUseCase crearPerfilUseCase) {
        this.crearPerfilUseCase = crearPerfilUseCase;
    }

    @PostMapping("/completar")
    public ResponseEntity<PerfilResultado> completarPerfil(@RequestBody CompletarPerfilRequest request) {
        CrearPerfilCommand comando = new CrearPerfilCommand(
                request.cuentaUsuarioId(),
                request.documentoIdentificacion(),
                request.nombreCompleto(),
                request.nombreComercial(),
                request.rol(),
                request.correoElectronico(),
                request.telefono(),
                request.latitud(),
                request.longitud()
        );
        
        PerfilResultado resultado = crearPerfilUseCase.ejecutar(comando);
        return ResponseEntity.ok(resultado);
    }
}
