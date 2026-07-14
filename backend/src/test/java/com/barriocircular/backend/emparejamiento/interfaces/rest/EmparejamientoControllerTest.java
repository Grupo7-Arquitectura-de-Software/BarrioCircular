package com.barriocircular.backend.emparejamiento.interfaces.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.emparejamiento.aplicacion.casosdeuso.CalcularOfertasOptimasUseCase;
import com.barriocircular.backend.emparejamiento.aplicacion.comandos.BuscarOfertasOptimasCommand;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.OfertaRecomendadaResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.ResultadoEmparejamientoResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.CatalogoPublicacionesNoDisponibleException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.emparejamiento.dominio.modelo.excepciones.FiltroInvalidoException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EmparejamientoControllerTest {

  private CalcularOfertasOptimasUseCase calcularOfertasOptimasUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    calcularOfertasOptimasUseCase = mock(CalcularOfertasOptimasUseCase.class);
    EmparejamientoController controlador =
        new EmparejamientoController(calcularOfertasOptimasUseCase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controlador)
            .setControllerAdvice(new EmparejamientoExceptionHandler())
            .build();
  }

  @Test
  void buscarOfertasOptimasDevuelve200ConResultados() throws Exception {
    UUID resultadoId = UUID.randomUUID();
    UUID compradorId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();

    ResultadoEmparejamientoResultado resultadoEsperado =
        new ResultadoEmparejamientoResultado(
            resultadoId,
            compradorId,
            -0.18,
            -78.48,
            10.0,
            Instant.now(),
            List.of(
                new OfertaRecomendadaResultado(publicacionId, 2.5, new BigDecimal("0.5"), 85.0)));

    when(calcularOfertasOptimasUseCase.ejecutar(
            any(BuscarOfertasOptimasCommand.class), eq("user_123")))
        .thenReturn(resultadoEsperado);

    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": 10.0,
                      "tiposMaterial": ["PET", "CARTON"],
                      "zonaDescriptiva": "Centro"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultadoId").value(resultadoId.toString()))
        .andExpect(jsonPath("$.ofertas[0].publicacionId").value(publicacionId.toString()))
        .andExpect(jsonPath("$.ofertas[0].distanciaKm").value(2.5))
        .andExpect(jsonPath("$.ofertas[0].precioPorKilo").value(0.5))
        .andExpect(jsonPath("$.ofertas[0].scoreTotal").value(85.0));
  }

  @Test
  void buscarOfertasOptimasLanzaExcepcionSiAutenticacionFalta() throws Exception {
    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": 10.0,
                      "tiposMaterial": ["PET"]
                    }
                    """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void buscarOfertasOptimasDevuelve404SiPerfilNoEncontrado() throws Exception {
    when(calcularOfertasOptimasUseCase.ejecutar(
            any(BuscarOfertasOptimasCommand.class), eq("user_123")))
        .thenThrow(new PerfilNoEncontradoException());

    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": 10.0,
                      "tiposMaterial": ["PET"]
                    }
                    """))
        .andExpect(status().isNotFound());
  }

  @Test
  void buscarOfertasOptimasDevuelve403SiPerfilNoAutorizado() throws Exception {
    when(calcularOfertasOptimasUseCase.ejecutar(
            any(BuscarOfertasOptimasCommand.class), eq("user_123")))
        .thenThrow(new PerfilNoAutorizadoException("No autorizado"));

    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": 10.0,
                      "tiposMaterial": ["PET"]
                    }
                    """))
        .andExpect(status().isForbidden());
  }

  @Test
  void buscarOfertasOptimasDevuelve400SiFiltroInvalido() throws Exception {
    when(calcularOfertasOptimasUseCase.ejecutar(
            any(BuscarOfertasOptimasCommand.class), eq("user_123")))
        .thenThrow(new FiltroInvalidoException("Filtro invalido"));

    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": -5.0,
                      "tiposMaterial": ["PET"]
                    }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void buscarOfertasOptimasDevuelve503SiCatalogoNoDisponible() throws Exception {
    when(calcularOfertasOptimasUseCase.ejecutar(
            any(BuscarOfertasOptimasCommand.class), eq("user_123")))
        .thenThrow(
            new CatalogoPublicacionesNoDisponibleException(
                "No disponible", new RuntimeException()));

    mockMvc
        .perform(
            post("/api/emparejamiento/buscar")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "latitud": -0.18,
                      "longitud": -78.48,
                      "radioMaximoKm": 10.0,
                      "tiposMaterial": ["PET"]
                    }
                    """))
        .andExpect(status().isServiceUnavailable());
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
