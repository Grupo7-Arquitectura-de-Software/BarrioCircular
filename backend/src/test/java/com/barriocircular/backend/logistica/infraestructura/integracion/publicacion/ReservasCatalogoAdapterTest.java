package com.barriocircular.backend.logistica.infraestructura.integracion.publicacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.CorreoElectronico;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.EstadoSesion;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorCuenta;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorUsuarioClerk;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.ListarMisReservasUseCase;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservasCatalogoAdapterTest {

  @Mock private ListarMisReservasUseCase listarMisReservasUseCase;

  @Mock private PerfilUsuarioRepository perfilUsuarioRepository;

  @Mock private CuentaAccesoRepositorio cuentaAccesoRepositorio;

  @Test
  void obtieneReservasActivasDelRecicladorTraducidasAlDtoDeLogistica() {
    UUID recicladorId = UUID.randomUUID();
    UUID cuentaId = UUID.randomUUID();
    String clerkId = "user_reciclador";
    Instant fechaReserva = Instant.parse("2026-07-09T14:00:00Z");
    PerfilUsuario perfil =
        perfil(recicladorId, cuentaId, RolUsuario.RECICLADOR, EstadoPerfil.ACTIVO);
    CuentaAcceso cuenta = cuenta(cuentaId, clerkId);
    PublicacionResultado reservada =
        publicacion(
            UUID.randomUUID(),
            UUID.randomUUID(),
            recicladorId,
            "CARTON",
            "RESERVADA",
            fechaReserva);
    PublicacionResultado finalizada =
        publicacion(
            UUID.randomUUID(), UUID.randomUUID(), recicladorId, "PET", "FINALIZADA", fechaReserva);
    PublicacionResultado reservadaPorOtro =
        publicacion(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "VIDRIO",
            "RESERVADA",
            fechaReserva);

    when(perfilUsuarioRepository.buscarPorId(recicladorId)).thenReturn(Optional.of(perfil));
    when(cuentaAccesoRepositorio.buscarPorId(cuentaId)).thenReturn(Optional.of(cuenta));
    when(listarMisReservasUseCase.ejecutar(clerkId))
        .thenReturn(List.of(reservada, finalizada, reservadaPorOtro));

    ReservasCatalogoAdapter adapter =
        new ReservasCatalogoAdapter(
            listarMisReservasUseCase, perfilUsuarioRepository, cuentaAccesoRepositorio);

    List<ReservaCatalogo> reservas = adapter.obtenerReservasActivasPorReciclador(recicladorId);

    assertEquals(1, reservas.size());
    ReservaCatalogo reserva = reservas.get(0);
    assertEquals(reservada.publicacionId(), reserva.publicacionId());
    assertEquals(reservada.creadorId(), reserva.vendedorId());
    assertEquals(reservada.tipoResiduo(), reserva.tipoResiduo());
    assertEquals(reservada.pesoKg(), reserva.pesoKg());
    assertEquals(reservada.latitud(), reserva.latitud());
    assertEquals(reservada.longitud(), reserva.longitud());
    assertEquals(fechaReserva, reserva.fechaReserva());
  }

  @Test
  void retornaListaVaciaSiNoPuedeResolverElClerkDelReciclador() {
    UUID recicladorId = UUID.randomUUID();
    when(perfilUsuarioRepository.buscarPorId(recicladorId)).thenReturn(Optional.empty());
    ReservasCatalogoAdapter adapter =
        new ReservasCatalogoAdapter(
            listarMisReservasUseCase, perfilUsuarioRepository, cuentaAccesoRepositorio);

    assertTrue(adapter.obtenerReservasActivasPorReciclador(recicladorId).isEmpty());
  }

  @Test
  void retornaListaVaciaSiElPerfilNoEsRecicladorActivo() {
    UUID perfilId = UUID.randomUUID();
    PerfilUsuario perfil =
        perfil(perfilId, UUID.randomUUID(), RolUsuario.CIUDADANO, EstadoPerfil.ACTIVO);
    when(perfilUsuarioRepository.buscarPorId(perfilId)).thenReturn(Optional.of(perfil));
    ReservasCatalogoAdapter adapter =
        new ReservasCatalogoAdapter(
            listarMisReservasUseCase, perfilUsuarioRepository, cuentaAccesoRepositorio);

    assertTrue(adapter.obtenerReservasActivasPorReciclador(perfilId).isEmpty());
  }

  private PublicacionResultado publicacion(
      UUID publicacionId,
      UUID vendedorId,
      UUID reservadoPor,
      String tipoResiduo,
      String estado,
      Instant fechaCreacion) {
    return new PublicacionResultado(
        publicacionId,
        vendedorId,
        tipoResiduo,
        12.5,
        new BigDecimal("0.20"),
        -0.1907,
        -78.4684,
        "https://example.com/evidencia.jpg",
        estado,
        fechaCreacion,
        reservadoPor,
        "Vendedor Demo",
        "0991234567");
  }

  private PerfilUsuario perfil(
      UUID perfilId, UUID cuentaId, RolUsuario rol, EstadoPerfil estadoPerfil) {
    return PerfilUsuario.reconstituir(
        perfilId,
        cuentaId,
        new DocumentoIdentificacion("1712345678"),
        "Reciclador Demo",
        null,
        rol,
        estadoPerfil,
        new InformacionContacto("reciclador@example.com", "0991234567"),
        new CoordenadaGPS(-0.180653, -78.467838),
        LocalDateTime.now());
  }

  private CuentaAcceso cuenta(UUID cuentaId, String clerkId) {
    return CuentaAcceso.reconstruir(
        new IdentificadorCuenta(cuentaId),
        new IdentificadorUsuarioClerk(clerkId),
        new CorreoElectronico("reciclador@example.com"),
        EstadoSesion.ACTIVA);
  }
}
