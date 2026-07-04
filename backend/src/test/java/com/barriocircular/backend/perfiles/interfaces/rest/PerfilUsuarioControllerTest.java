package com.barriocircular.backend.perfiles.interfaces.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.CrearPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PerfilUsuarioControllerTest {

  private CrearPerfilUseCase crearPerfilUseCase;
  private ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    crearPerfilUseCase = mock(CrearPerfilUseCase.class);
    obtenerPerfilPorClerkIdUseCase = mock(ObtenerPerfilPorClerkIdUseCase.class);
    PerfilUsuarioController controlador =
        new PerfilUsuarioController(
            crearPerfilUseCase, obtenerPerfilPorClerkIdUseCase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controlador)
            .setControllerAdvice(new PerfilUsuarioExceptionHandler())
            .build();
  }

  @Test
  void obtenerMiPerfilDevuelve200SiExiste() throws Exception {
    PerfilResultado perfilEsperado = crearPerfilResultado(UUID.randomUUID());
    when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123")).thenReturn(perfilEsperado);

    mockMvc
        .perform(get("/api/perfiles/me").principal(autenticacion("user_123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cuentaUsuarioId").value(perfilEsperado.cuentaUsuarioId().toString()))
        .andExpect(jsonPath("$.rol").value("CIUDADANO"));
  }

  @Test
  void obtenerMiPerfilDevuelve404SiNoExiste() throws Exception {
    when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123"))
        .thenThrow(new PerfilNoEncontradoException());

    mockMvc
        .perform(get("/api/perfiles/me").principal(autenticacion("user_123")))
        .andExpect(status().isNotFound());
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

  private PerfilResultado crearPerfilResultado(UUID cuentaUsuarioId) {
    return new PerfilResultado(
        UUID.randomUUID(),
        cuentaUsuarioId,
        "1712345678",
        "Ana Perez",
        null,
        "CIUDADANO",
        "ACTIVO",
        "ana@correo.com",
        "0999999999",
        -0.1807,
        -78.4678);
  }
}
