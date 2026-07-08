package com.barriocircular.backend.verificacionidentidad.interfaces.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso.EmitirCredencialUseCase;
import com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso.VerificarCredencialUseCase;
import com.barriocircular.backend.verificacionidentidad.aplicacion.dto.ResultadoVerificacionPublico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class VerificacionIdentidadSecurityTest {

  private EmitirCredencialUseCase emitirCredencialUseCase;
  private VerificarCredencialUseCase verificarCredencialUseCase;
  private MockMvc mockMvc;

  @BeforeEach
  void configurar() {
    emitirCredencialUseCase = org.mockito.Mockito.mock(EmitirCredencialUseCase.class);
    verificarCredencialUseCase = org.mockito.Mockito.mock(VerificarCredencialUseCase.class);

    VerificacionIdentidadController controller =
        new VerificacionIdentidadController(emitirCredencialUseCase, verificarCredencialUseCase);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new VerificacionIdentidadExceptionHandler())
            .build();
  }

  @Test
  void endpointPublicoFuncionaSinJwt() throws Exception {
    String token = "token-publico-de-prueba-000000000000000";
    when(verificarCredencialUseCase.ejecutar(token))
        .thenReturn(ResultadoVerificacionPublico.invalido());

    mockMvc
        .perform(get("/api/verificacion-identidad/publico/{token}", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valido").value(false));
  }

  @Test
  void endpointEmisionRequiereJwt() throws Exception {
    mockMvc
        .perform(post("/api/verificacion-identidad/credenciales"))
        .andExpect(status().isUnauthorized());
  }
}
