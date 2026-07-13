package com.barriocircular.backend.sugerenciaprecio.infraestructura.integracion.groq;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.AnalizadorMaterialIAPort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Conformista frente a GroQ: adopta el formato de respuesta que el modelo devuelve sin intentar
 * renegociar el contrato. Nunca propaga excepciones de red/timeout/parseo — devuelve {@link
 * Optional#empty()} para que el caso de uso registre un análisis IA_NO_DISPONIBLE.
 *
 * <p>El análisis siempre es sobre una foto, por lo que siempre usa el modelo con visión ({@code
 * groq.vision-model}). {@code temperature=0} y {@code response_format=json_object} para que la
 * misma foto produzca un veredicto estable y parseable en vez de variar en cada llamada.
 */
@Component
public class GroqAnalizadorMaterialAdapter implements AnalizadorMaterialIAPort {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final String visionModel;

  @Autowired
  public GroqAnalizadorMaterialAdapter(
      @Value("${groq.api-key}") String apiKey,
      @Value("${groq.vision-model}") String visionModel,
      @Value("${groq.base-url}") String baseUrl,
      ObjectMapper objectMapper) {
    this(apiKey, visionModel, baseUrl, RestClient.builder(), objectMapper);
  }

  GroqAnalizadorMaterialAdapter(
      String apiKey,
      String visionModel,
      String baseUrl,
      RestClient.Builder restClientBuilder,
      ObjectMapper objectMapper) {
    this.visionModel = visionModel;
    this.objectMapper = objectMapper;
    this.restClient =
        restClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .build();
  }

  @Override
  public Optional<AnalisisIA> analizar(String imagenBase64) {
    try {
      Map<String, Object> cuerpoSolicitud =
          Map.of(
              "model",
              visionModel,
              "temperature",
              0,
              "response_format",
              Map.of("type", "json_object"),
              "messages",
              List.of(
                  Map.of(
                      "role",
                      "user",
                      "content",
                      List.of(
                          Map.of("type", "text", "text", construirPrompt()),
                          Map.of("type", "image_url", "image_url", Map.of("url", imagenBase64))))));

      GroqChatResponse respuesta =
          restClient
              .post()
              .uri("/chat/completions")
              .contentType(MediaType.APPLICATION_JSON)
              .body(cuerpoSolicitud)
              .retrieve()
              .body(GroqChatResponse.class);

      String contenido = extraerContenido(respuesta);
      return contenido == null ? Optional.empty() : parsearAnalisis(contenido);
    } catch (Exception fallaDeProveedorIA) {
      return Optional.empty();
    }
  }

  private String construirPrompt() {
    return """
        Eres un inspector de materiales reciclables para un marketplace de reciclaje en
        Quito, Ecuador. Analiza la foto adjunta y responde EXCLUSIVAMENTE con un JSON con
        este formato exacto, sin texto adicional, sin explicacion fuera del JSON, sin
        bloques de markdown:
        {"esMaterialReciclaje": <true o false>, "fotoClara": <true o false>, "multiplesMateriales": <true o false>, "tipoMaterial": <"PET", "CARTON", "VIDRIO", "CHATARRA", "OTRO" o null>, "pesoEstimadoKg": <numero decimal o null>, "estadoMaterial": <"EXCELENTE", "BUENO", "REGULAR" o null>, "recomendacion": <"frase breve en espanol" o null>}

        Reglas:
        1. esMaterialReciclaje: true solo si la foto muestra principalmente material
           reciclable (envases plasticos, carton o papel, vidrio, metal). Personas,
           mascotas, paisajes, comida, objetos en uso o capturas de pantalla -> false, y
           explica en "recomendacion" que se ve en la foto.
        2. fotoClara: false si la foto esta borrosa, muy oscura, sobreexpuesta, o el
           material se ve muy lejano o cortado. En ese caso da en "recomendacion" una
           instruccion concreta y accionable (ej.: "Acercate mas al material", "Toma la
           foto con mas luz", "Enfoca antes de disparar").
        3. multiplesMateriales: true solo si aparecen mezclados materiales de TIPOS
           distintos (ej. botellas PET junto a carton). Varias unidades del mismo material
           NO cuentan como multiples. En ese caso pide en "recomendacion" mostrar un solo
           tipo de material.
        4. tipoMaterial: PET = botellas y envases de plastico PET; CARTON = cajas y laminas
           de carton o papel; VIDRIO = botellas y frascos de vidrio; CHATARRA = metal,
           latas, hierro, aluminio. Si es reciclable pero no encaja en esas 4 categorias
           (ej. ropa, madera, electronicos), responde "OTRO". Si esMaterialReciclaje es
           false, responde null.
        5. pesoEstimadoKg: estima el peso total SOLO del material visible siguiendo este
           metodo: (a) identifica las unidades visibles y su tamano; (b) cuenta o aproxima
           cuantas unidades hay (si estan apiladas o en fundas, estima por volumen);
           (c) multiplica por el peso unitario de referencia; (d) redondea a 1 decimal.
           Pesos unitarios de referencia:
           - PET: botella de 500 ml vacia = 0.02 kg; botella de 2-3 L = 0.05 kg;
             funda o saco lleno de botellas = 2 a 4 kg.
           - CARTON: caja pequena plegada = 0.2 kg; caja mediana = 0.5 kg; caja grande =
             1 kg; pila de laminas de 30 cm de alto = 5 kg aprox.
           - VIDRIO: botella = 0.4 kg; frasco = 0.25 kg; jaba o caja llena de botellas =
             8 kg aprox.
           - CHATARRA: lata de aluminio = 0.015 kg; funda llena de latas = 1 kg;
             olla o pieza metalica mediana = 1 a 3 kg; pieza grande (tubo, plancha,
             electrodomestico) = 5 a 20 kg.
           El resultado debe ser coherente con lo que se ve: una foto tipica de reciclaje
           domestico suele pesar entre 0.5 y 30 kg; sospecha de cualquier resultado mayor
           a 100 y recalcula. Casi siempre es posible dar un estimado aproximado; responde
           null solo si el material casi no se distingue. Minimo 0.1 (una sola botella o
           lata cuenta como 0.1).
        6. estadoMaterial: EXCELENTE = limpio, seco y sin danos; BUENO = uso normal con
           suciedad leve; REGULAR = sucio, humedo, aplastado o deteriorado. null si
           esMaterialReciclaje es false.
        7. Si la foto es valida, "recomendacion" puede ser null o un consejo breve para
           mejorar la venta.
        """;
  }

  private String extraerContenido(GroqChatResponse respuesta) {
    if (respuesta == null || respuesta.choices() == null || respuesta.choices().isEmpty()) {
      return null;
    }
    GroqChoice primerChoice = respuesta.choices().get(0);
    return primerChoice.message() == null ? null : primerChoice.message().content();
  }

  private Optional<AnalisisIA> parsearAnalisis(String contenidoCrudo) {
    try {
      String contenidoJson = limpiarBloquesMarkdown(contenidoCrudo);
      JsonNode nodo = objectMapper.readTree(contenidoJson);
      return Optional.of(
          new AnalisisIA(
              leerBooleano(nodo, "esMaterialReciclaje"),
              leerBooleano(nodo, "fotoClara"),
              leerBooleano(nodo, "multiplesMateriales"),
              leerTexto(nodo, "tipoMaterial"),
              leerDecimal(nodo, "pesoEstimadoKg"),
              leerTexto(nodo, "estadoMaterial"),
              leerTexto(nodo, "recomendacion")));
    } catch (Exception jsonInvalido) {
      return Optional.empty();
    }
  }

  private Boolean leerBooleano(JsonNode nodo, String campo) {
    JsonNode valor = nodo.get(campo);
    return valor == null || !valor.isBoolean() ? null : valor.asBoolean();
  }

  private String leerTexto(JsonNode nodo, String campo) {
    return nodo.hasNonNull(campo) ? nodo.get(campo).asText() : null;
  }

  private Double leerDecimal(JsonNode nodo, String campo) {
    JsonNode valor = nodo.get(campo);
    return valor == null || !valor.isNumber() ? null : valor.asDouble();
  }

  private String limpiarBloquesMarkdown(String contenido) {
    return contenido.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
  }

  private record GroqChatResponse(List<GroqChoice> choices) {}

  private record GroqChoice(GroqMessage message) {}

  private record GroqMessage(String content) {}
}
