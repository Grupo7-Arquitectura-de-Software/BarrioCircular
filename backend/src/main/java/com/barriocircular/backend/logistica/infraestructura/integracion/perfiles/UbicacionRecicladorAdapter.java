package com.barriocircular.backend.logistica.infraestructura.integracion.perfiles;

import com.barriocircular.backend.logistica.aplicacion.puertos.UbicacionRecicladorPort;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UbicacionRecicladorAdapter implements UbicacionRecicladorPort {

  private final PerfilUsuarioRepository perfilUsuarioRepository;

  public UbicacionRecicladorAdapter(PerfilUsuarioRepository perfilUsuarioRepository) {
    this.perfilUsuarioRepository =
        Objects.requireNonNull(
            perfilUsuarioRepository, "El repositorio de perfiles es obligatorio.");
  }

  @Override
  public Optional<CoordenadaGPS> obtenerUbicacionActual(UUID recicladorId) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");

    return perfilUsuarioRepository
        .buscarPorId(recicladorId)
        .filter(this::esRecicladorActivo)
        .map(PerfilUsuario::getUbicacionHabitual)
        .map(ubicacion -> new CoordenadaGPS(ubicacion.latitud(), ubicacion.longitud()));
  }

  private boolean esRecicladorActivo(PerfilUsuario perfil) {
    return perfil.getRol() == RolUsuario.RECICLADOR
        && perfil.getEstadoPerfil() == EstadoPerfil.ACTIVO
        && perfil.getUbicacionHabitual() != null;
  }
}
