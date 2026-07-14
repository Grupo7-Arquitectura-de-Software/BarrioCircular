package com.barriocircular.backend.logistica.infraestructura.integracion.perfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UbicacionRecicladorAdapterTest {

  @Mock private PerfilUsuarioRepository perfilUsuarioRepository;

  @Test
  void traduceUbicacionDeRecicladorActivoACoordenadaPropiaDeLogistica() {
    UUID recicladorId = UUID.randomUUID();
    PerfilUsuario perfil =
        perfil(recicladorId, RolUsuario.RECICLADOR, EstadoPerfil.ACTIVO, -0.180653, -78.467838);
    when(perfilUsuarioRepository.buscarPorId(recicladorId)).thenReturn(Optional.of(perfil));

    UbicacionRecicladorAdapter adapter = new UbicacionRecicladorAdapter(perfilUsuarioRepository);

    Optional<CoordenadaGPS> ubicacion = adapter.obtenerUbicacionActual(recicladorId);

    assertTrue(ubicacion.isPresent());
    assertEquals(-0.180653, ubicacion.get().latitud());
    assertEquals(-78.467838, ubicacion.get().longitud());
  }

  @Test
  void retornaVacioSiElPerfilNoEsRecicladorActivo() {
    UUID recicladorId = UUID.randomUUID();
    PerfilUsuario perfil =
        perfil(
            recicladorId,
            RolUsuario.CENTRO_RECOLECCION,
            EstadoPerfil.ACTIVO,
            -0.180653,
            -78.467838);
    when(perfilUsuarioRepository.buscarPorId(recicladorId)).thenReturn(Optional.of(perfil));

    UbicacionRecicladorAdapter adapter = new UbicacionRecicladorAdapter(perfilUsuarioRepository);

    assertTrue(adapter.obtenerUbicacionActual(recicladorId).isEmpty());
  }

  private PerfilUsuario perfil(
      UUID perfilId, RolUsuario rol, EstadoPerfil estadoPerfil, double latitud, double longitud) {
    return PerfilUsuario.reconstituir(
        perfilId,
        UUID.randomUUID(),
        new DocumentoIdentificacion("1712345678"),
        rol == RolUsuario.CENTRO_RECOLECCION ? null : "Reciclador Demo",
        rol == RolUsuario.CENTRO_RECOLECCION ? "Centro Demo" : null,
        rol,
        estadoPerfil,
        new InformacionContacto("reciclador@example.com", "0991234567"),
        new com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS(
            latitud, longitud),
        LocalDateTime.now());
  }
}
