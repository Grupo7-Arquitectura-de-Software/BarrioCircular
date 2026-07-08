package com.barriocircular.backend.emparejamiento.aplicacion.casodeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.emparejamiento.aplicacion.casosdeuso.CalcularOfertasOptimasUseCase;
import com.barriocircular.backend.emparejamiento.aplicacion.comandos.BuscarOfertasOptimasCommand;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.PerfilCapacidadesComprador;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.ResultadoEmparejamientoResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.CatalogoPublicacionesNoDisponibleException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.emparejamiento.aplicacion.puertos.CatalogoPublicacionesPort;
import com.barriocircular.backend.emparejamiento.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.emparejamiento.dominio.modelo.agregado.ResultadoEmparejamiento;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.TipoMaterialFiltro;
import com.barriocircular.backend.emparejamiento.dominio.repositorios.EmparejamientoRepositorio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CrearCalculoOfertasOptimas {

  @Mock private CatalogoPublicacionesPort catalogoPublicacionesPort;

  @Mock private PerfilConsultor perfilConsultor;

  @Mock private EmparejamientoRepositorio emparejamientoRepositorio;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private CalcularOfertasOptimasUseCase calcularOfertasOptimasUseCase;

  private BuscarOfertasOptimasCommand comandoValido;
  private final String clerkIdAutenticado = "user_123";

  @BeforeEach
  void setUp() {
    comandoValido =
        new BuscarOfertasOptimasCommand(
            -0.18, -78.48, 10.0, Set.of("CARTON", "PET"), "Centro");
  }

  @Test
  void calculaOfertasOptimasExitosamente() {
    UUID perfilId = UUID.randomUUID();
    PerfilCapacidadesComprador perfilCapacidades =
        new PerfilCapacidadesComprador(perfilId, true);
    when(perfilConsultor.obtenerCapacidadesPorClerkId(clerkIdAutenticado))
        .thenReturn(Optional.of(perfilCapacidades));

    OfertaCatalogo oferta =
        new OfertaCatalogo(
            UUID.randomUUID(),
            TipoMaterialFiltro.CARTON,
            10.0,
            new BigDecimal("0.5"),
            -0.19,
            -78.49,
            "DISPONIBLE");
    when(catalogoPublicacionesPort.obtenerCatalogoDisponible()).thenReturn(List.of(oferta));

    when(emparejamientoRepositorio.guardar(any(ResultadoEmparejamiento.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ResultadoEmparejamientoResultado resultado =
        calcularOfertasOptimasUseCase.ejecutar(comandoValido, clerkIdAutenticado);

    assertNotNull(resultado);
    assertEquals(perfilId, resultado.compradorId());
    assertEquals(-0.18, resultado.latitudOrigen());
    assertEquals(-78.48, resultado.longitudOrigen());
    assertFalse(resultado.ofertas().isEmpty());

    verify(emparejamientoRepositorio, times(1)).guardar(any(ResultadoEmparejamiento.class));
    verify(eventPublisher, atLeastOnce()).publishEvent(any(Object.class));
  }

  @Test
  void lanzaExcepcionSiPerfilNoEncontrado() {
    when(perfilConsultor.obtenerCapacidadesPorClerkId(clerkIdAutenticado))
        .thenReturn(Optional.empty());

    assertThrows(
        PerfilNoEncontradoException.class,
        () -> calcularOfertasOptimasUseCase.ejecutar(comandoValido, clerkIdAutenticado));

    verify(catalogoPublicacionesPort, never()).obtenerCatalogoDisponible();
    verify(emparejamientoRepositorio, never()).guardar(any());
  }

  @Test
  void lanzaExcepcionSiPerfilNoAutorizado() {
    UUID perfilId = UUID.randomUUID();
    PerfilCapacidadesComprador perfilCapacidades =
        new PerfilCapacidadesComprador(perfilId, false);
    when(perfilConsultor.obtenerCapacidadesPorClerkId(clerkIdAutenticado))
        .thenReturn(Optional.of(perfilCapacidades));

    assertThrows(
        PerfilNoAutorizadoException.class,
        () -> calcularOfertasOptimasUseCase.ejecutar(comandoValido, clerkIdAutenticado));

    verify(catalogoPublicacionesPort, never()).obtenerCatalogoDisponible();
    verify(emparejamientoRepositorio, never()).guardar(any());
  }

  @Test
  void lanzaExcepcionSiCatalogoNoDisponible() {
    UUID perfilId = UUID.randomUUID();
    PerfilCapacidadesComprador perfilCapacidades =
        new PerfilCapacidadesComprador(perfilId, true);
    when(perfilConsultor.obtenerCapacidadesPorClerkId(clerkIdAutenticado))
        .thenReturn(Optional.of(perfilCapacidades));

    when(catalogoPublicacionesPort.obtenerCatalogoDisponible())
        .thenThrow(new RuntimeException("Error de conexion"));

    assertThrows(
        CatalogoPublicacionesNoDisponibleException.class,
        () -> calcularOfertasOptimasUseCase.ejecutar(comandoValido, clerkIdAutenticado));

    verify(emparejamientoRepositorio, never()).guardar(any());
  }
}
