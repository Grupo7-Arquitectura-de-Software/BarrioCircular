package com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.SugerirPrecioCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaPrecioResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.SugeridorPrecioIAPort;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.PrecioSugeridoInvalidoException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.FuenteSugerencia;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.SugerenciaPrecioRepositorio;
import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Este caso de uso no debe fallar nunca por causas relacionadas con la IA: el peor escenario es
 * responder con el precio de catálogo, nunca un error 5xx por culpa de GroQ.
 */
@Service
public class SugerirPrecioUseCase {

  private static final String JUSTIFICACION_IA_NO_DISPONIBLE =
      "Precio de referencia de catálogo: la IA no pudo generar una sugerencia para estos datos.";

  private final SugerenciaPrecioRepositorio sugerenciaPrecioRepositorio;
  private final SugeridorPrecioIAPort sugeridorPrecioIAPort;
  private final CatalogoPreciosReferencia catalogoPreciosReferencia;

  public SugerirPrecioUseCase(
      SugerenciaPrecioRepositorio sugerenciaPrecioRepositorio,
      SugeridorPrecioIAPort sugeridorPrecioIAPort,
      CatalogoPreciosReferencia catalogoPreciosReferencia) {
    this.sugerenciaPrecioRepositorio = sugerenciaPrecioRepositorio;
    this.sugeridorPrecioIAPort = sugeridorPrecioIAPort;
    this.catalogoPreciosReferencia = catalogoPreciosReferencia;
  }

  @Transactional
  public SugerenciaPrecioResultado ejecutar(
      SugerirPrecioCommand command, String clerkIdSolicitante) {
    TipoMaterialSugerido tipoMaterial = TipoMaterialSugerido.desde(command.tipoResiduo());

    PrecioSugerido precioSugerido;
    FuenteSugerencia fuente;
    String justificacion;

    Optional<SugerenciaIA> sugerenciaIA =
        consultarIASinPropagarFallos(tipoMaterial, command.pesoKg(), command.imagenBase64());
    PrecioSugerido precioValidado = sugerenciaIA.map(this::validarPrecioDeIA).orElse(null);

    if (precioValidado != null && sugerenciaIA.get().materialCoincide()) {
      precioSugerido = precioValidado;
      fuente = FuenteSugerencia.IA_GROQ;
      justificacion = sugerenciaIA.get().justificacion();
    } else if (precioValidado != null) {
      precioSugerido = catalogoPreciosReferencia.precioDeReferencia(tipoMaterial);
      fuente = FuenteSugerencia.CATALOGO_RESPALDO;
      justificacion =
          "La foto no parece corresponder al material declarado ("
              + tipoMaterial.name()
              + "). Se usó un precio de referencia; revisa la imagen antes de publicar.";
    } else {
      precioSugerido = catalogoPreciosReferencia.precioDeReferencia(tipoMaterial);
      fuente = FuenteSugerencia.CATALOGO_RESPALDO;
      justificacion = JUSTIFICACION_IA_NO_DISPONIBLE;
    }

    SugerenciaPrecio sugerenciaPrecio =
        SugerenciaPrecio.generar(
            tipoMaterial,
            command.pesoKg(),
            precioSugerido,
            fuente,
            justificacion,
            clerkIdSolicitante);

    sugerenciaPrecioRepositorio.guardar(sugerenciaPrecio);

    return SugerenciaPrecioResultado.desde(sugerenciaPrecio);
  }

  private Optional<SugerenciaIA> consultarIASinPropagarFallos(
      TipoMaterialSugerido tipoMaterial, Double pesoKg, String imagenBase64) {
    try {
      return sugeridorPrecioIAPort.sugerirPrecio(tipoMaterial, pesoKg, imagenBase64);
    } catch (Exception excepcionInesperada) {
      return Optional.empty();
    }
  }

  private PrecioSugerido validarPrecioDeIA(SugerenciaIA sugerenciaIA) {
    try {
      return new PrecioSugerido(sugerenciaIA.precioPorKilo());
    } catch (PrecioSugeridoInvalidoException fueraDeRango) {
      return null;
    }
  }
}
