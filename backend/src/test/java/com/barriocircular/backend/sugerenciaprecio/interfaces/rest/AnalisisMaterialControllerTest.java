package com.barriocircular.backend.sugerenciaprecio.interfaces.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso.AnalizarMaterialUseCase;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.AnalizarMaterialCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisMaterialResultado;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.ImagenAnalisisInvalidaException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.ResultadoAnalisis;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
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

class AnalisisMaterialControllerTest {

  private AnalizarMaterialUseCase analizarMaterialUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    analizarMaterialUseCase = mock(AnalizarMaterialUseCase.class);
    AnalisisMaterialController controlador =
        new AnalisisMaterialController(analizarMaterialUseCase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controlador)
            .setControllerAdvice(new AnalisisMaterialExceptionHandler())
            .build();
  }

  @Test
  void devuelve200ConLasSugerenciasCuandoLaFotoEsValida() throws Exception {
    AnalisisMaterialResultado resultado =
        new AnalisisMaterialResultado(
            UUID.randomUUID(),
            ResultadoAnalisis.VALIDO,
            TipoMaterialSugerido.PET,
            2.5,
            EstadoMaterial.BUENO,
            new BigDecimal("0.27"),
            "Botellas en buen estado.",
            Instant.now());
    when(analizarMaterialUseCase.ejecutar(any(AnalizarMaterialCommand.class), eq("user_123")))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/analisis-material")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "imagenBase64": "data:image/jpeg;base64,ZmFrZQ==" }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultado").value("VALIDO"))
        .andExpect(jsonPath("$.tipoMaterial").value("PET"))
        .andExpect(jsonPath("$.pesoEstimadoKg").value(2.5))
        .andExpect(jsonPath("$.estadoMaterial").value("BUENO"))
        .andExpect(jsonPath("$.precioSugeridoPorKilo").value(0.27));
  }

  @Test
  void devuelve200ConElRechazoCuandoLaFotoNoEsDeReciclaje() throws Exception {
    AnalisisMaterialResultado resultado =
        new AnalisisMaterialResultado(
            UUID.randomUUID(),
            ResultadoAnalisis.NO_ES_RECICLAJE,
            null,
            null,
            null,
            null,
            "La foto muestra un gato.",
            Instant.now());
    when(analizarMaterialUseCase.ejecutar(any(AnalizarMaterialCommand.class), eq("user_123")))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/analisis-material")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "imagenBase64": "data:image/jpeg;base64,ZmFrZQ==" }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultado").value("NO_ES_RECICLAJE"))
        .andExpect(jsonPath("$.recomendacion").value("La foto muestra un gato."));
  }

  @Test
  void devuelve400SiLaImagenNoEsUnDataUriValido() throws Exception {
    when(analizarMaterialUseCase.ejecutar(any(AnalizarMaterialCommand.class), eq("user_123")))
        .thenThrow(new ImagenAnalisisInvalidaException());

    mockMvc
        .perform(
            post("/api/analisis-material")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    { "imagenBase64": "https://foto.jpg" }
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void devuelve401SiNoHayIdentidadAutenticada() throws Exception {
    mockMvc
        .perform(
            post("/api/analisis-material")
                .contentType("application/json")
                .content(
                    """
                    { "imagenBase64": "data:image/jpeg;base64,ZmFrZQ==" }
                    """))
        .andExpect(status().isUnauthorized());
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
