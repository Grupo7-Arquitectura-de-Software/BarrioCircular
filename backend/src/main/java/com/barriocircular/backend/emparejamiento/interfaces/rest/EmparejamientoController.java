package com.barriocircular.backend.emparejamiento.interfaces.rest;

import com.barriocircular.backend.emparejamiento.aplicacion.casosdeuso.CalcularOfertasOptimasUseCase;
import com.barriocircular.backend.emparejamiento.aplicacion.comandos.BuscarOfertasOptimasCommand;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.ResultadoEmparejamientoResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.IdentidadAutenticadaNoDisponibleException;
import com.barriocircular.backend.emparejamiento.interfaces.rest.dto.BuscarOfertasRequest;
import com.barriocircular.backend.emparejamiento.interfaces.rest.dto.OfertaRecomendadaResponse;
import com.barriocircular.backend.emparejamiento.interfaces.rest.dto.ResultadoEmparejamientoResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emparejamiento")
public class EmparejamientoController {

  private final CalcularOfertasOptimasUseCase calcularOfertasOptimasUseCase;

  public EmparejamientoController(CalcularOfertasOptimasUseCase calcularOfertasOptimasUseCase) {
    this.calcularOfertasOptimasUseCase = calcularOfertasOptimasUseCase;
  }

  @PostMapping("/buscar")
  public ResponseEntity<ResultadoEmparejamientoResponse> buscarOfertasOptimas(
      @RequestBody BuscarOfertasRequest solicitud, Authentication autenticacion) {
    String clerkId = extraerClerkId(autenticacion);
    BuscarOfertasOptimasCommand comando = crearComando(solicitud);
    ResultadoEmparejamientoResultado resultado =
        calcularOfertasOptimasUseCase.ejecutar(comando, clerkId);
    return ResponseEntity.ok(convertirRespuesta(resultado));
  }

  private BuscarOfertasOptimasCommand crearComando(BuscarOfertasRequest solicitud) {
    return new BuscarOfertasOptimasCommand(
        solicitud.latitud(),
        solicitud.longitud(),
        solicitud.radioMaximoKm(),
        solicitud.tiposMaterial(),
        solicitud.zonaDescriptiva(),
        solicitud.pesoMinimo(),
        solicitud.pesoMaximo());
  }

  private ResultadoEmparejamientoResponse convertirRespuesta(
      ResultadoEmparejamientoResultado resultado) {
    List<OfertaRecomendadaResponse> ofertas =
        resultado.ofertas().stream()
            .map(
                oferta ->
                    new OfertaRecomendadaResponse(
                        oferta.publicacionId(),
                        oferta.distanciaKm(),
                        oferta.precioPorKilo(),
                        oferta.scoreTotal()))
            .toList();

    return new ResultadoEmparejamientoResponse(
        resultado.resultadoId(), resultado.fechaCalculo(), ofertas);
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
