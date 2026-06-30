package com.barriocircular.backend.perfiles.interfaces.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.CrearPerfilUseCase;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;

class PerfilUsuarioControllerTest {

    private CrearPerfilUseCase crearPerfilUseCase;
    private ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase;
    private CuentaAccesoConsultor cuentaAccesoConsultor;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        crearPerfilUseCase = mock(CrearPerfilUseCase.class);
        obtenerPerfilPorClerkIdUseCase = mock(ObtenerPerfilPorClerkIdUseCase.class);
        cuentaAccesoConsultor = mock(CuentaAccesoConsultor.class);
        PerfilUsuarioController controlador = new PerfilUsuarioController(
                crearPerfilUseCase, obtenerPerfilPorClerkIdUseCase, cuentaAccesoConsultor);
        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setControllerAdvice(new PerfilUsuarioExceptionHandler())
                .build();
    }

    @Test
    void obtenerMiPerfilDevuelve200SiExiste() throws Exception {
        PerfilResultado perfilEsperado = crearPerfilResultado(UUID.randomUUID());
        when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123")).thenReturn(perfilEsperado);

        mockMvc.perform(get("/api/perfiles/me").principal(autenticacion("user_123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuentaUsuarioId").value(perfilEsperado.cuentaUsuarioId().toString()))
                .andExpect(jsonPath("$.rol").value("CIUDADANO"));
    }

    @Test
    void obtenerMiPerfilDevuelve404SiNoExiste() throws Exception {
        when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123"))
                .thenThrow(new PerfilNoEncontradoException());

        mockMvc.perform(get("/api/perfiles/me").principal(autenticacion("user_123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void completarPerfilRechazaUnaCuentaAjena() throws Exception {
        UUID cuentaUsuarioAutenticadaId = UUID.randomUUID();
        UUID cuentaUsuarioAjenaId = UUID.randomUUID();
        when(cuentaAccesoConsultor.obtenerCuentaIdPorClerkId("user_123"))
                .thenReturn(Optional.of(cuentaUsuarioAutenticadaId));

        String cuerpoSolicitud = """
                {
                  "cuentaUsuarioId": "%s",
                  "documentoIdentificacion": "1712345678",
                  "nombreCompleto": "Ana Perez",
                  "nombreComercial": null,
                  "rol": "CIUDADANO",
                  "correoElectronico": "ana@correo.com",
                  "telefono": "0999999999",
                  "latitud": -0.1807,
                  "longitud": -78.4678
                }
                """.formatted(cuentaUsuarioAjenaId);

        mockMvc.perform(post("/api/perfiles/completar")
                        .principal(autenticacion("user_123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoSolicitud))
                .andExpect(status().isForbidden());
        verify(crearPerfilUseCase, never()).ejecutar(any(CrearPerfilCommand.class));
    }

    private JwtAuthenticationToken autenticacion(String clerkId) {
        Jwt jwt = new Jwt(
                "token", Instant.now(), Instant.now().plusSeconds(300),
                Map.of("alg", "none"), Map.of("sub", clerkId));
        return new JwtAuthenticationToken(jwt);
    }

    private PerfilResultado crearPerfilResultado(UUID cuentaUsuarioId) {
        return new PerfilResultado(
                UUID.randomUUID(), cuentaUsuarioId, "1712345678", "Ana Perez", null,
                "CIUDADANO", "ACTIVO", "ana@correo.com", "0999999999",
                -0.1807, -78.4678);
    }
}
