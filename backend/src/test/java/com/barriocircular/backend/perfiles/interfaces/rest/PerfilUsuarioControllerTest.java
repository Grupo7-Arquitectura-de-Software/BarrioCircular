package com.barriocircular.backend.perfiles.interfaces.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ActualizarMiPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.CrearPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.comandos.ActualizarMiPerfilCommand;
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
  private ActualizarMiPerfilUseCase actualizarMiPerfilUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    crearPerfilUseCase = mock(CrearPerfilUseCase.class);
    obtenerPerfilPorClerkIdUseCase = mock(ObtenerPerfilPorClerkIdUseCase.class);
    actualizarMiPerfilUseCase = mock(ActualizarMiPerfilUseCase.class);
    PerfilUsuarioController controlador =
        new PerfilUsuarioController(
            crearPerfilUseCase, obtenerPerfilPorClerkIdUseCase, actualizarMiPerfilUseCase);
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

  @Test
  void actualizarMiPerfilDevuelve200ConPerfilActualizado() throws Exception {
    PerfilResultado perfilEsperado = crearPerfilResultado(UUID.randomUUID());
    PerfilResultado perfilActualizado =
        new PerfilResultado(
            perfilEsperado.perfilId(),
            perfilEsperado.cuentaUsuarioId(),
            perfilEsperado.documentoIdentificacion(),
            "Ana Gomez",
            perfilEsperado.nombreComercial(),
            perfilEsperado.rol(),
            perfilEsperado.estadoPerfil(),
            perfilEsperado.correoElectronico(),
            "0987654321",
            -0.18,
            -78.48,
            "Av. Amazonas");
    when(actualizarMiPerfilUseCase.ejecutar(
            new ActualizarMiPerfilCommand(
                "user_123", "Ana", "Gomez", "0987654321", "Av. Amazonas", -0.18, -78.48)))
        .thenReturn(perfilActualizado);

    mockMvc
        .perform(
            patch("/api/perfiles/me")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    {
                      "nombre": "Ana",
                      "apellido": "Gomez",
                      "telefono": "0987654321",
                      "direccion": "Av. Amazonas",
                      "latitud": -0.18,
                      "longitud": -78.48
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombreCompleto").value("Ana Gomez"))
        .andExpect(jsonPath("$.telefono").value("0987654321"))
        .andExpect(jsonPath("$.direccionHabitual").value("Av. Amazonas"));
  }

  @Test
  void actualizarMiPerfilRechazaCamposRestringidos() throws Exception {
    mockMvc
        .perform(
            patch("/api/perfiles/me")
                .principal(autenticacion("user_123"))
                .contentType("application/json")
                .content(
                    """
                    {
                      "telefono": "0987654321",
                      "rol": "CENTRO_RECOLECCION",
                      "perfilId": "41cc3221-5d9a-4e47-9642-74c55d4637f6"
                    }
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
        -78.4678,
        null);
  }
}
