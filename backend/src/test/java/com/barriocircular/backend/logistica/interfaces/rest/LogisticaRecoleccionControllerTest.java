package com.barriocircular.backend.logistica.interfaces.rest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ActualizarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ConfirmarRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ConstruirRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.FinalizarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.IniciarRutaRecoleccionUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ObtenerRutaActivaUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.ObtenerRutaPorIdUseCase;
import com.barriocircular.backend.logistica.aplicacion.casosdeuso.RegistrarLlegadaParadaUseCase;
import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.CoordenadaRutaResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.ParadaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.excepciones.SinReservasElegiblesException;
import com.barriocircular.backend.perfiles.aplicacion.casosdeuso.ObtenerPerfilPorClerkIdUseCase;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

class LogisticaRecoleccionControllerTest {

  private ConstruirRutaRecoleccionUseCase construirRutaRecoleccionUseCase;
  private ObtenerRutaActivaUseCase obtenerRutaActivaUseCase;
  private ActualizarRutaRecoleccionUseCase actualizarRutaRecoleccionUseCase;
  private IniciarRutaRecoleccionUseCase iniciarRutaRecoleccionUseCase;
  private FinalizarRutaRecoleccionUseCase finalizarRutaRecoleccionUseCase;
  private ObtenerRutaPorIdUseCase obtenerRutaPorIdUseCase;
  private RegistrarLlegadaParadaUseCase registrarLlegadaParadaUseCase;
  private ConfirmarRecoleccionUseCase confirmarRecoleccionUseCase;
  private ObtenerPerfilPorClerkIdUseCase obtenerPerfilPorClerkIdUseCase;
  private MockMvc mockMvc;
  private UUID recicladorId;

  @BeforeEach
  void configurar() {
    construirRutaRecoleccionUseCase = mock(ConstruirRutaRecoleccionUseCase.class);
    obtenerRutaActivaUseCase = mock(ObtenerRutaActivaUseCase.class);
    actualizarRutaRecoleccionUseCase = mock(ActualizarRutaRecoleccionUseCase.class);
    iniciarRutaRecoleccionUseCase = mock(IniciarRutaRecoleccionUseCase.class);
    finalizarRutaRecoleccionUseCase = mock(FinalizarRutaRecoleccionUseCase.class);
    obtenerRutaPorIdUseCase = mock(ObtenerRutaPorIdUseCase.class);
    registrarLlegadaParadaUseCase = mock(RegistrarLlegadaParadaUseCase.class);
    confirmarRecoleccionUseCase = mock(ConfirmarRecoleccionUseCase.class);
    obtenerPerfilPorClerkIdUseCase = mock(ObtenerPerfilPorClerkIdUseCase.class);
    recicladorId = UUID.randomUUID();
    when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123")).thenReturn(perfilReciclador());

    LogisticaRecoleccionController controlador =
        new LogisticaRecoleccionController(
            construirRutaRecoleccionUseCase,
            obtenerRutaActivaUseCase,
            actualizarRutaRecoleccionUseCase,
            obtenerRutaPorIdUseCase,
            registrarLlegadaParadaUseCase,
            confirmarRecoleccionUseCase,
            iniciarRutaRecoleccionUseCase,
            finalizarRutaRecoleccionUseCase,
            obtenerPerfilPorClerkIdUseCase);
    mockMvc = MockMvcBuilders.standaloneSetup(controlador)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void construirRutaDevuelve201ConRutaPlanificada() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultado();
    when(construirRutaRecoleccionUseCase.ejecutar(
            eq(recicladorId), eq(LocalTime.of(9, 0)), eq(LocalDate.of(2026, 7, 9))))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/logistica/rutas")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "fechaRuta": "2026-07-09",
                      "horaInicioRuta": "09:00:00"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rutaId").value(resultado.rutaId().toString()))
        .andExpect(jsonPath("$.estado").value("PLANIFICADA"))
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653))
        .andExpect(jsonPath("$.origen.longitud").value(-78.467838))
        .andExpect(jsonPath("$.paradas[0].orden").value(1))
        .andExpect(jsonPath("$.paradas[0].tipoResiduo").value("CARTON"))
        .andExpect(jsonPath("$.paradas[0].pesoKg").value(12.5));
  }

  @Test
  void construirRutaSinReservasElegiblesDevuelve422() throws Exception {
    when(construirRutaRecoleccionUseCase.ejecutar(
            eq(recicladorId), eq(LocalTime.of(9, 0)), eq(LocalDate.of(2026, 7, 9))))
        .thenThrow(
            new SinReservasElegiblesException(
                "No existen reservas activas para construir la ruta."));

    mockMvc
        .perform(
            post("/api/logistica/rutas")
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "fechaRuta": "2026-07-09",
                      "horaInicioRuta": "09:00:00"
                    }
                    """))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error").value("No existen reservas activas para construir la ruta."));
  }

  @Test
  void obtenerRutaActivaDevuelve200SiExiste() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultado();
    when(obtenerRutaActivaUseCase.ejecutar(recicladorId)).thenReturn(Optional.of(resultado));

    mockMvc
        .perform(get("/api/logistica/rutas/activa").principal(autenticacion("user_123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rutaId").value(resultado.rutaId().toString()))
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653))
        .andExpect(jsonPath("$.origen.longitud").value(-78.467838));
  }

  @Test
  void obtenerRutaPorIdDevuelve200SiExiste() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultado();
    when(obtenerRutaPorIdUseCase.ejecutar(resultado.rutaId())).thenReturn(Optional.of(resultado));

    mockMvc
        .perform(
            get("/api/logistica/rutas/{id}", resultado.rutaId())
                .principal(autenticacion("user_123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rutaId").value(resultado.rutaId().toString()))
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653))
        .andExpect(jsonPath("$.origen.longitud").value(-78.467838))
        .andExpect(jsonPath("$.paradas[0].tipoResiduo").value("CARTON"))
        .andExpect(jsonPath("$.paradas[0].pesoKg").value(12.5));
  }

  @Test
  void registrarLlegadaDevuelve200ConParadaEnProgreso() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultadoConLlegada();
    UUID rutaId = resultado.rutaId();
    UUID paradaId = resultado.paradas().get(0).paradaId();
    when(registrarLlegadaParadaUseCase.ejecutar(
            eq(rutaId), eq(paradaId), eq(LocalDate.of(2026, 7, 9)), eq(LocalTime.of(10, 15))))
        .thenReturn(resultado);

    mockMvc
        .perform(
            patch("/api/logistica/rutas/{rutaId}/paradas/{paradaId}/llegada", rutaId, paradaId)
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "fechaLlegada": "2026-07-09",
                      "horaLlegada": "10:15:00"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653))
        .andExpect(jsonPath("$.origen.longitud").value(-78.467838))
        .andExpect(jsonPath("$.paradas[0].estado").value("EN_PROGRESO"));
  }

  @Test
  void confirmarRecoleccionDevuelve200ConResultadoMinimo() throws Exception {
    UUID rutaId = UUID.randomUUID();
    UUID paradaId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    ConfirmacionRecoleccionResultado resultado =
        new ConfirmacionRecoleccionResultado(
            rutaId, "EN_CURSO", paradaId, "COMPLETADA", publicacionId, "FINALIZADA", 11.4, false);
    when(confirmarRecoleccionUseCase.ejecutar(
            eq(recicladorId), eq(rutaId), eq(paradaId), eq(11.4), eq("Material humedo.")))
        .thenReturn(resultado);

    mockMvc
        .perform(
            post("/api/logistica/rutas/{rutaId}/paradas/{paradaId}/confirmar", rutaId, paradaId)
                .principal(autenticacion("user_123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "pesoRealVerificado": 11.4,
                      "observaciones": "Material humedo."
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rutaId").value(rutaId.toString()))
        .andExpect(jsonPath("$.paradaId").value(paradaId.toString()))
        .andExpect(jsonPath("$.publicacionId").value(publicacionId.toString()))
        .andExpect(jsonPath("$.estadoParada").value("COMPLETADA"))
        .andExpect(jsonPath("$.estadoPublicacion").value("FINALIZADA"))
        .andExpect(jsonPath("$.pesoRealVerificado").value(11.4))
        .andExpect(jsonPath("$.rutaTerminada").value(false));
  }

  @Test
  void actualizarRutaActivaDevuelve200ConRutaReplanificada() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultado();
    when(actualizarRutaRecoleccionUseCase.ejecutar(recicladorId)).thenReturn(resultado);

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                    "/api/logistica/rutas/activa")
                .principal(autenticacion("user_123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rutaId").value(resultado.rutaId().toString()))
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653))
        .andExpect(jsonPath("$.paradas[0].tipoResiduo").value("CARTON"));
  }

  @Test
  void finalizarRutaActivaDevuelve200ConRutaCompletada() throws Exception {
    RutaRecoleccionResultado resultado = rutaResultadoCompletada();
    when(finalizarRutaRecoleccionUseCase.ejecutar(recicladorId)).thenReturn(resultado);

    mockMvc
        .perform(post("/api/logistica/rutas/activa/finalizar").principal(autenticacion("user_123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rutaId").value(resultado.rutaId().toString()))
        .andExpect(jsonPath("$.estado").value("COMPLETADA"))
        .andExpect(jsonPath("$.origen.latitud").value(-0.180653));
  }

  @Test
  void rechazaUsuarioQueNoEsReciclador() throws Exception {
    when(obtenerPerfilPorClerkIdUseCase.ejecutar("user_123")).thenReturn(perfilCiudadano());

    mockMvc
        .perform(get("/api/logistica/rutas/activa").principal(autenticacion("user_123")))
        .andExpect(status().isForbidden());
  }

  @Test
  void rechazaSolicitudSinAutenticacion() throws Exception {
    mockMvc.perform(get("/api/logistica/rutas/activa")).andExpect(status().isUnauthorized());
  }

  private RutaRecoleccionResultado rutaResultado() {
    UUID paradaId = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    return new RutaRecoleccionResultado(
        UUID.randomUUID(),
        "PLANIFICADA",
        LocalDate.of(2026, 7, 9),
        new CoordenadaRutaResultado(-0.180653, -78.467838),
        List.of(
            new ParadaRecoleccionResultado(
                paradaId,
                publicacionId,
                "CARTON",
                12.5,
                "PENDIENTE",
                1,
                java.time.ZonedDateTime.parse("2026-07-09T09:05:00-05:00[America/Guayaquil]"),
                null,
                -0.1907,
                -78.4684)));
  }

  private RutaRecoleccionResultado rutaResultadoConLlegada() {
    RutaRecoleccionResultado resultado = rutaResultado();
    ParadaRecoleccionResultado parada = resultado.paradas().get(0);
    return new RutaRecoleccionResultado(
        resultado.rutaId(),
        "EN_CURSO",
        resultado.fecha(),
        resultado.origen(),
        List.of(
            new ParadaRecoleccionResultado(
                parada.paradaId(),
                parada.publicacionId(),
                parada.tipoResiduo(),
                parada.pesoKg(),
                "EN_PROGRESO",
                parada.orden(),
                parada.horaLlegadaEstimada(),
                java.time.ZonedDateTime.parse("2026-07-09T10:15:00-05:00[America/Guayaquil]"),
                parada.latitud(),
                parada.longitud())));
  }

  private RutaRecoleccionResultado rutaResultadoCompletada() {
    RutaRecoleccionResultado resultado = rutaResultadoConLlegada();
    return new RutaRecoleccionResultado(
        resultado.rutaId(),
        "COMPLETADA",
        resultado.fecha(),
        resultado.origen(),
        resultado.paradas());
  }

  private PerfilResultado perfilReciclador() {
    return new PerfilResultado(
        recicladorId,
        UUID.randomUUID(),
        "1712345678",
        "Reciclador Demo",
        null,
        "RECICLADOR",
        "ACTIVO",
        "reciclador@example.com",
        "0999999999",
        -0.180653,
        -78.467838,
        null);
  }

  private PerfilResultado perfilCiudadano() {
    return new PerfilResultado(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "1712345678",
        "Ciudadano Demo",
        null,
        "CIUDADANO",
        "ACTIVO",
        "ciudadano@example.com",
        "0999999999",
        -0.180653,
        -78.467838,
        null);
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
