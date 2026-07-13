package com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.AnalizarMaterialCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisMaterialResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.AnalizadorMaterialIAPort;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.ImagenAnalisisInvalidaException;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.ResultadoAnalisis;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.AnalisisMaterialRepositorio;
import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Analiza la foto de un material antes de publicar: valida que sea reciclaje, que se vea bien y que
 * muestre un solo material; si todo está en orden sugiere tipo, peso y precio. Este caso de uso no
 * debe fallar nunca por causas relacionadas con la IA: el peor escenario es un análisis
 * IA_NO_DISPONIBLE que deja al usuario completar el formulario a mano, nunca un 5xx por culpa de
 * GroQ. El precio jamás lo decide la IA: se calcula del catálogo de precios de mercado ajustado por
 * el estado del material que la IA observó.
 */
@Service
public class AnalizarMaterialUseCase {

  private static final double PESO_MINIMO_KG = 0.1;
  private static final double PESO_MAXIMO_KG = 1000.0;
  private static final int LONGITUD_MAXIMA_RECOMENDACION = 500;
  private static final String RECOMENDACION_FOTO_NO_CLARA_POR_DEFECTO =
      "Vuelve a tomar la foto con mejor luz y más cerca del material.";

  private final AnalisisMaterialRepositorio analisisMaterialRepositorio;
  private final AnalizadorMaterialIAPort analizadorMaterialIAPort;
  private final CatalogoPreciosReferencia catalogoPreciosReferencia;

  public AnalizarMaterialUseCase(
      AnalisisMaterialRepositorio analisisMaterialRepositorio,
      AnalizadorMaterialIAPort analizadorMaterialIAPort,
      CatalogoPreciosReferencia catalogoPreciosReferencia) {
    this.analisisMaterialRepositorio = analisisMaterialRepositorio;
    this.analizadorMaterialIAPort = analizadorMaterialIAPort;
    this.catalogoPreciosReferencia = catalogoPreciosReferencia;
  }

  @Transactional
  public AnalisisMaterialResultado ejecutar(
      AnalizarMaterialCommand command, String clerkIdSolicitante) {
    validarImagen(command.imagenBase64());

    AnalisisMaterial analisis =
        consultarIASinPropagarFallos(command.imagenBase64())
            .map(analisisIA -> interpretar(analisisIA, clerkIdSolicitante))
            .orElseGet(
                () -> sinSugerencias(ResultadoAnalisis.IA_NO_DISPONIBLE, null, clerkIdSolicitante));

    analisisMaterialRepositorio.guardar(analisis);

    return AnalisisMaterialResultado.desde(analisis);
  }

  private void validarImagen(String imagenBase64) {
    if (imagenBase64 == null || !imagenBase64.startsWith("data:image/")) {
      throw new ImagenAnalisisInvalidaException();
    }
  }

  private Optional<AnalisisIA> consultarIASinPropagarFallos(String imagenBase64) {
    try {
      return analizadorMaterialIAPort.analizar(imagenBase64);
    } catch (Exception excepcionInesperada) {
      return Optional.empty();
    }
  }

  /**
   * Traduce la respuesta cruda de la IA a un análisis del dominio, en orden de prioridad y sin
   * confiar en el modelo: veredictos incompletos equivalen a IA no disponible, y las sugerencias
   * solo se aceptan si pasan las validaciones (tipo del catálogo, peso en rango, estado conocido).
   */
  private AnalisisMaterial interpretar(AnalisisIA analisisIA, String clerkIdSolicitante) {
    String recomendacion = normalizarRecomendacion(analisisIA.recomendacion());

    if (analisisIA.esMaterialReciclaje() == null
        || analisisIA.fotoClara() == null
        || analisisIA.multiplesMateriales() == null) {
      return sinSugerencias(ResultadoAnalisis.IA_NO_DISPONIBLE, null, clerkIdSolicitante);
    }
    if (!analisisIA.esMaterialReciclaje()) {
      return sinSugerencias(ResultadoAnalisis.NO_ES_RECICLAJE, recomendacion, clerkIdSolicitante);
    }
    if (!analisisIA.fotoClara()) {
      return sinSugerencias(
          ResultadoAnalisis.FOTO_NO_CLARA,
          recomendacion != null ? recomendacion : RECOMENDACION_FOTO_NO_CLARA_POR_DEFECTO,
          clerkIdSolicitante);
    }
    if (analisisIA.multiplesMateriales()) {
      return sinSugerencias(
          ResultadoAnalisis.MULTIPLES_MATERIALES, recomendacion, clerkIdSolicitante);
    }

    TipoMaterialSugerido tipoMaterial = parsearTipoMaterial(analisisIA.tipoMaterial());
    if (tipoMaterial == null) {
      return sinSugerencias(
          ResultadoAnalisis.MATERIAL_NO_SOPORTADO, recomendacion, clerkIdSolicitante);
    }

    EstadoMaterial estadoMaterial =
        EstadoMaterial.desde(analisisIA.estadoMaterial()).orElse(EstadoMaterial.BUENO);
    Double pesoEstimadoKg = validarPesoEstimado(analisisIA.pesoEstimadoKg());
    PrecioSugerido precioSugerido =
        catalogoPreciosReferencia.precioSugerido(tipoMaterial, estadoMaterial);

    return AnalisisMaterial.generar(
        ResultadoAnalisis.VALIDO,
        tipoMaterial,
        pesoEstimadoKg,
        estadoMaterial,
        precioSugerido,
        recomendacion,
        clerkIdSolicitante);
  }

  private AnalisisMaterial sinSugerencias(
      ResultadoAnalisis resultado, String recomendacion, String clerkIdSolicitante) {
    return AnalisisMaterial.generar(
        resultado, null, null, null, null, recomendacion, clerkIdSolicitante);
  }

  private TipoMaterialSugerido parsearTipoMaterial(String tipoMaterial) {
    try {
      return TipoMaterialSugerido.desde(tipoMaterial);
    } catch (TipoMaterialSugeridoInvalidoException fueraDelCatalogo) {
      return null;
    }
  }

  /** Un peso fuera de rango es una alucinación: se descarta en vez de ajustarlo a la fuerza. */
  private Double validarPesoEstimado(Double pesoEstimadoKg) {
    if (pesoEstimadoKg == null
        || pesoEstimadoKg < PESO_MINIMO_KG
        || pesoEstimadoKg > PESO_MAXIMO_KG) {
      return null;
    }
    return pesoEstimadoKg;
  }

  private String normalizarRecomendacion(String recomendacion) {
    if (recomendacion == null || recomendacion.isBlank()) {
      return null;
    }
    String recortada = recomendacion.trim();
    return recortada.length() <= LONGITUD_MAXIMA_RECOMENDACION
        ? recortada
        : recortada.substring(0, LONGITUD_MAXIMA_RECOMENDACION);
  }
}
