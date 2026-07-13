package com.barriocircular.backend.sugerenciaprecio.infraestructura.integracion.groq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisIA;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

class GroqAnalizadorMaterialAdapterTest {

  private static final String IMAGEN_BASE64 = "data:image/jpeg;base64,ZmFrZS1pbWFnZQ==";

  private MockRestServiceServer servidorSimulado;
  private GroqAnalizadorMaterialAdapter adapter;

  @BeforeEach
  void configurar() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://fake-groq");
    servidorSimulado = MockRestServiceServer.bindTo(builder).build();
    adapter =
        new GroqAnalizadorMaterialAdapter(
            "fake-api-key", "vision-model-test", "http://fake-groq", builder, new ObjectMapper());
  }

  @Test
  void devuelveElAnalisisCompletoCuandoGroqRespondeUnJsonValido() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(jsonPath("$.model").value("vision-model-test"))
        .andExpect(jsonPath("$.messages[0].content[1].image_url.url").value(IMAGEN_BASE64))
        .andRespond(
            withSuccess(
                """
                {
                  "choices": [
                    { "message": { "content": "{\\"esMaterialReciclaje\\": true, \\"fotoClara\\": true, \\"multiplesMateriales\\": false, \\"tipoMaterial\\": \\"PET\\", \\"pesoEstimadoKg\\": 2.5, \\"estadoMaterial\\": \\"BUENO\\", \\"recomendacion\\": \\"Botellas limpias\\"}" } }
                  ]
                }
                """,
                MediaType.APPLICATION_JSON));

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isPresent());
    AnalisisIA analisis = resultado.get();
    assertTrue(analisis.esMaterialReciclaje());
    assertTrue(analisis.fotoClara());
    assertFalse(analisis.multiplesMateriales());
    assertEquals("PET", analisis.tipoMaterial());
    assertEquals(2.5, analisis.pesoEstimadoKg());
    assertEquals("BUENO", analisis.estadoMaterial());
    assertEquals("Botellas limpias", analisis.recomendacion());
  }

  @Test
  void toleraCamposNulosYBloquesMarkdownEnLaRespuesta() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "```json\\n{\\"esMaterialReciclaje\\": false, \\"fotoClara\\": true, \\"multiplesMateriales\\": false, \\"tipoMaterial\\": null, \\"pesoEstimadoKg\\": null, \\"estadoMaterial\\": null, \\"recomendacion\\": \\"La foto muestra un gato\\"}\\n```" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isPresent());
    assertFalse(resultado.get().esMaterialReciclaje());
    assertNull(resultado.get().tipoMaterial());
    assertNull(resultado.get().pesoEstimadoKg());
    assertEquals("La foto muestra un gato", resultado.get().recomendacion());
  }

  @Test
  void devuelveVeredictosNulosCuandoFaltanEnElJsonSinInventarValores() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "{\\"tipoMaterial\\": \\"PET\\"}" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isPresent());
    assertNull(resultado.get().esMaterialReciclaje());
    assertNull(resultado.get().fotoClara());
    assertNull(resultado.get().multiplesMateriales());
  }

  @Test
  void devuelveVacioCuandoElContenidoNoEsJsonValido() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "esto no es json" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isEmpty());
  }

  @Test
  void devuelveVacioCuandoLaRespuestaVieneSinChoices() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(withSuccess("{ \"choices\": [] }", MediaType.APPLICATION_JSON));

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isEmpty());
  }

  @Test
  void devuelveVacioCuandoLaLlamadaFallaPorTimeoutORed() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            request -> {
              throw new IOException("timeout simulado");
            });

    Optional<AnalisisIA> resultado = adapter.analizar(IMAGEN_BASE64);

    assertTrue(resultado.isEmpty());
  }
}
