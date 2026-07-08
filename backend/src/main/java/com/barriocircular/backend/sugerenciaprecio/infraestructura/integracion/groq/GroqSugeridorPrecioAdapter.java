package com.barriocircular.backend.sugerenciaprecio.infraestructura.integracion.groq;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.SugeridorPrecioIAPort;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Conformist frente a GroQ: adopta el formato de respuesta que el modelo devuelve sin intentar
 * renegociar el contrato. Nunca propaga excepciones de red/timeout/parseo — devuelve {@link
 * Optional#empty()} para que el caso de uso active el catálogo de respaldo.
 *
 * <p>Cuando se recibe una foto de evidencia (data URI base64), la petición usa el modelo con visión
 * ({@code groq.vision-model}) y envía la imagen como parte del contenido del mensaje; sin foto, usa
 * el modelo de solo texto ({@code groq.model}). {@code temperature=0} para que la misma combinación
 * de material/peso/foto produzca una sugerencia estable en vez de variar en cada llamada.
 */
@Component
public class GroqSugeridorPrecioAdapter implements SugeridorPrecioIAPort {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final String model;
  private final String visionModel;

  @Autowired
  public GroqSugeridorPrecioAdapter(
      @Value("${groq.api-key}") String apiKey,
      @Value("${groq.model}") String model,
      @Value("${groq.vision-model}") String visionModel,
      @Value("${groq.base-url}") String baseUrl,
      ObjectMapper objectMapper) {
    this(apiKey, model, visionModel, baseUrl, RestClient.builder(), objectMapper);
  }

  GroqSugeridorPrecioAdapter(
      String apiKey,
      String model,
      String visionModel,
      String baseUrl,
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper) {
    this.model = model;
    this.visionModel = visionModel;
    this.objectMapper = objectMapper;
    this.restClient =
        restClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .build();
  }

  @Override
  public Optional<SugerenciaIA> sugerirPrecio(
      TipoMaterialSugerido tipoMaterial, Double pesoKg, String imagenBase64) {
    try {
      boolean conImagen = StringUtils.hasText(imagenBase64);
      Map<String, Object> cuerpoSolicitud =
          Map.of(
              "model",
              conImagen ? visionModel : model,
              "temperature",
              0,
              "messages",
              List.of(
                  Map.of(
                      "role",
                      "user",
                      "content",
                      construirContenido(tipoMaterial, pesoKg, conImagen, imagenBase64))));

      GroqChatResponse respuesta =
          restClient
              .post()
              .uri("/chat/completions")
              .contentType(MediaType.APPLICATION_JSON)
              .body(cuerpoSolicitud)
              .retrieve()
              .body(GroqChatResponse.class);

      String contenido = extraerContenido(respuesta);
      return contenido == null ? Optional.empty() : parsearSugerencia(contenido);
    } catch (Exception fallaDeProveedorIA) {
      return Optional.empty();
    }
  }

  private Object construirContenido(
      TipoMaterialSugerido tipoMaterial, Double pesoKg, boolean conImagen, String imagenBase64) {
    String prompt = construirPrompt(tipoMaterial, pesoKg, conImagen);
    if (!conImagen) {
      return prompt;
    }
    return List.of(
        Map.of("type", "text", "text", prompt),
        Map.of("type", "image_url", "image_url", Map.of("url", imagenBase64)));
  }

  private String construirPrompt(
      TipoMaterialSugerido tipoMaterial, Double pesoKg, boolean conImagen) {
    String instruccionImagen =
        conImagen
            ? "Observa la foto adjunta: usa el estado de conservación, limpieza y cantidad "
                + "visible para ajustar el precio respecto a un valor de referencia estándar. "
                + "Ademas, valida si la foto realmente muestra un material reciclable del tipo "
                + "declarado ("
                + tipoMaterial.name()
                + "). Si la imagen no corresponde a ese material, no parece un residuo "
                + "reciclable, o no tiene relación alguna con reciclaje, responde "
                + "\"materialCoincide\": false y explica brevemente el motivo en "
                + "\"justificacion\"."
            : "";
    String lineaPeso =
        pesoKg == null ? "Peso: no especificado" : "Peso aproximado disponible: " + pesoKg + " kg";

    return """
        Eres un experto en el mercado de reciclaje de materiales en Quito, Ecuador.
        %s
        Responde EXCLUSIVAMENTE con un JSON con este formato exacto, sin texto
        adicional, sin explicacion fuera del JSON, sin bloques de markdown:
        {"precioPorKiloUsd": <numero decimal>, "justificacion": "<una frase breve>", "materialCoincide": <true o false>}

        Material declarado: %s
        %s
        """
        .formatted(instruccionImagen, tipoMaterial.name(), lineaPeso);
  }

  private String extraerContenido(GroqChatResponse respuesta) {
    if (respuesta == null || respuesta.choices() == null || respuesta.choices().isEmpty()) {
      return null;
    }
    GroqChoice primerChoice = respuesta.choices().get(0);
    return primerChoice.message() == null ? null : primerChoice.message().content();
  }

  private Optional<SugerenciaIA> parsearSugerencia(String contenidoCrudo) {
    try {
      String contenidoJson = limpiarBloquesMarkdown(contenidoCrudo);
      JsonNode nodo = objectMapper.readTree(contenidoJson);
      JsonNode precioNodo = nodo.get("precioPorKiloUsd");
      if (precioNodo == null || !precioNodo.isNumber()) {
        return Optional.empty();
      }
      BigDecimal precio = precioNodo.decimalValue();
      String justificacion =
          nodo.hasNonNull("justificacion") ? nodo.get("justificacion").asText() : null;
      JsonNode materialCoincideNodo = nodo.get("materialCoincide");
      boolean materialCoincide =
          materialCoincideNodo == null
              || !materialCoincideNodo.isBoolean()
              || materialCoincideNodo.asBoolean();
      return Optional.of(new SugerenciaIA(precio, justificacion, materialCoincide));
    } catch (Exception jsonInvalido) {
      return Optional.empty();
    }
  }

  private String limpiarBloquesMarkdown(String contenido) {
    return contenido.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
  }

  private record GroqChatResponse(List<GroqChoice> choices) {}

  private record GroqChoice(GroqMessage message) {}

  private record GroqMessage(String content) {}
}
