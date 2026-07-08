package com.barriocircular.backend.sugerenciaprecio.infraestructura.integracion.groq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaIA;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

class GroqSugeridorPrecioAdapterTest {

  private MockRestServiceServer servidorSimulado;
  private GroqSugeridorPrecioAdapter adapter;

  @BeforeEach
  void configurar() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://fake-groq");
    servidorSimulado = MockRestServiceServer.bindTo(builder).build();
    adapter =
        new GroqSugeridorPrecioAdapter(
            "fake-api-key",
            "llama-3.1-8b-instant",
            "vision-model-test",
            "http://fake-groq",
            builder,
            new ObjectMapper());
  }

  @Test
  void devuelveLaSugerenciaCuandoGroqRespondeUnJsonValido() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withSuccess(
                """
                {
                  "choices": [
                    { "message": { "content": "{\\"precioPorKiloUsd\\": 0.42, \\"justificacion\\": \\"buen precio\\"}" } }
                  ]
                }
                """,
                MediaType.APPLICATION_JSON));

    Optional<SugerenciaIA> resultado = adapter.sugerirPrecio(TipoMaterialSugerido.PET, 10.0, null);

    assertTrue(resultado.isPresent());
    assertEquals(0, resultado.get().precioPorKilo().compareTo(new java.math.BigDecimal("0.42")));
    assertEquals("buen precio", resultado.get().justificacion());
  }

  @Test
  void usaElModeloDeVisionYEnviaLaImagenCuandoSeAdjuntaUnaFoto() {
    String imagenBase64 = "data:image/jpeg;base64,ZmFrZS1pbWFnZQ==";
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andExpect(jsonPath("$.model").value("vision-model-test"))
        .andExpect(jsonPath("$.messages[0].content[1].image_url.url").value(imagenBase64))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "{\\"precioPorKiloUsd\\": 0.5, \\"justificacion\\": \\"buen estado\\"}" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<SugerenciaIA> resultado =
        adapter.sugerirPrecio(TipoMaterialSugerido.PET, 10.0, imagenBase64);

    assertTrue(resultado.isPresent());
    assertEquals("buen estado", resultado.get().justificacion());
  }

  @Test
  void marcaMaterialNoCoincideCuandoLaImagenNoCorrespondeAlMaterialDeclarado() {
    String imagenBase64 = "data:image/jpeg;base64,ZmFrZS1pbWFnZQ==";
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "{\\"precioPorKiloUsd\\": 0.3, \\"justificacion\\": \\"la foto muestra un gato, no carton\\", \\"materialCoincide\\": false}" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<SugerenciaIA> resultado =
        adapter.sugerirPrecio(TipoMaterialSugerido.CARTON, 5.0, imagenBase64);

    assertTrue(resultado.isPresent());
    assertFalse(resultado.get().materialCoincide());
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

    Optional<SugerenciaIA> resultado =
        adapter.sugerirPrecio(TipoMaterialSugerido.CARTON, 5.0, null);

    assertTrue(resultado.isEmpty());
  }

  @Test
  void devuelveVacioCuandoFaltaElCampoPrecio() {
    servidorSimulado
        .expect(requestTo("http://fake-groq/chat/completions"))
        .andRespond(
            withSuccess(
                """
                { "choices": [ { "message": { "content": "{\\"justificacion\\": \\"sin precio\\"}" } } ] }
                """,
                MediaType.APPLICATION_JSON));

    Optional<SugerenciaIA> resultado =
        adapter.sugerirPrecio(TipoMaterialSugerido.VIDRIO, null, null);

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

    Optional<SugerenciaIA> resultado =
        adapter.sugerirPrecio(TipoMaterialSugerido.CHATARRA, 8.0, null);

    assertTrue(resultado.isEmpty());
  }
}
