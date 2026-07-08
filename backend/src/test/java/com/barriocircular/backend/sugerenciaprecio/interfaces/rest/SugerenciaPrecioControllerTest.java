package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso.SugerirPrecioUseCase;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.SugerirPrecioCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaPrecioResultado;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.FuenteSugerencia;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SugerenciaPrecioControllerTest {

  private SugerirPrecioUseCase sugerirPrecioUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    sugerirPrecioUseCase = mock(SugerirPrecioUseCase.class);
    SugerenciaPrecioController controlador = new SugerenciaPrecioController(sugerirPrecioUseCase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controlador)
            .setControllerAdvice(new SugerenciaPrecioExceptionHandler())
            .build();
  }

  @Test
  void devuelve200ConSugerenciaDeIaCuandoGroqRespondeCorrectamente() throws Exception {
    SugerenciaPrecioResultado resultado =
        new SugerenciaPrecioResultado(
            UUID.randomUUID(),
            new BigDecimal("0.45"),
            FuenteSugerencia.IA_GROQ,
            "buen precio",
            Instant.now());
    when(sugerirPrecioUseCase.ejecutar(any(SugerirPrecioCommand.class), eq("user_123")))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/sugerencias-precio")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "tipoResiduo": "PET", "pesoKg": 10.0 }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fuente").value("IA_GROQ"))
        .andExpect(jsonPath("$.precioSugeridoPorKilo").value(0.45));
  }

  @Test
  void devuelve200ConCatalogoDeRespaldoAunCuandoGroqEstaCaido() throws Exception {
    SugerenciaPrecioResultado resultado =
        new SugerenciaPrecioResultado(
            UUID.randomUUID(),
            new BigDecimal("0.30"),
            FuenteSugerencia.CATALOGO_RESPALDO,
            null,
            Instant.now());
    when(sugerirPrecioUseCase.ejecutar(any(SugerirPrecioCommand.class), eq("user_123")))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/sugerencias-precio")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "tipoResiduo": "PET", "pesoKg": 10.0 }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fuente").value("CATALOGO_RESPALDO"));
  }

  @Test
  void devuelve400SiElTipoDeMaterialNoPerteneceAlCatalogo() throws Exception {
    when(sugerirPrecioUseCase.ejecutar(any(SugerirPrecioCommand.class), eq("user_123")))
        .thenThrow(new TipoMaterialSugeridoInvalidoException("MADERA"));

    mockMvc
        .perform(
            post("/api/sugerencias-precio")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "tipoResiduo": "MADERA", "pesoKg": 10.0 }
                    """))
        .andExpect(status().isBadRequest());
  }

  private JwtAuthenticationToken autenticacion(String clerkId) {
    Jwt jwt =
        new Jwt(
            "token",
            Instant.now(),
            Instant.now().plusSeconds(300),
            Map.of("alg", "none"),
            Map.of("sub", clerkId));
    return new JwtAuthenticationToken(jwt);
  }
}
