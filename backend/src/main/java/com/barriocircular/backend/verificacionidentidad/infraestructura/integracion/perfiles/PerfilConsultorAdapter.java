package com.barriocircular.backend.verificacionidentidad.infraestructura.integracion.perfiles;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.EstadoSesion;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilElegible;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilVerificable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("verificacionIdentidadPerfilConsultorAdapter")
public class PerfilConsultorAdapter implements PerfilConsultor {

  private final CuentaAccesoRepositorio cuentaAccesoRepositorio;
  private final PerfilUsuarioRepository perfilUsuarioRepository;

  public PerfilConsultorAdapter(
      CuentaAccesoRepositorio cuentaAccesoRepositorio,
      PerfilUsuarioRepository perfilUsuarioRepository) {
    this.cuentaAccesoRepositorio = cuentaAccesoRepositorio;
    this.perfilUsuarioRepository = perfilUsuarioRepository;
  }

  @Override
  public Optional<PerfilElegible> obtenerPorClerkId(String clerkId) {
    return cuentaAccesoRepositorio
        .buscarPorClerkId(clerkId)
        .flatMap(
            cuenta ->
                perfilUsuarioRepository
                    .buscarPorCuentaUsuarioId(cuenta.getCuentaId().uuid())
                    .map(perfil -> aPerfilElegible(perfil, cuenta)));
  }

  @Override
  public Optional<PerfilVerificable> obtenerPorPerfilId(UUID perfilId) {
    return perfilUsuarioRepository
        .buscarPorId(perfilId)
        .flatMap(
            perfil ->
                cuentaAccesoRepositorio
                    .buscarPorId(perfil.getCuentaUsuarioId())
                    .map(cuenta -> aPerfilVerificable(perfil, cuenta)));
  }

  private PerfilElegible aPerfilElegible(PerfilUsuario perfil, CuentaAcceso cuenta) {
    return new PerfilElegible(
        perfil.getId(),
        nombreMostrado(perfil),
        perfil.getRol().name(),
        perfil.estaActivo(),
        cuentaActiva(cuenta),
        fechaRegistro(perfil));
  }

  private PerfilVerificable aPerfilVerificable(PerfilUsuario perfil, CuentaAcceso cuenta) {
    return new PerfilVerificable(
        perfil.getId(),
        nombreMostrado(perfil),
        perfil.getRol().name(),
        perfil.estaActivo(),
        cuentaActiva(cuenta),
        fechaRegistro(perfil));
  }

  private boolean cuentaActiva(CuentaAcceso cuenta) {
    return cuenta.getEstadoSesion() == EstadoSesion.ACTIVA;
  }

  private String nombreMostrado(PerfilUsuario perfil) {
    if (perfil.getNombreComercial() != null && !perfil.getNombreComercial().isBlank()) {
      return perfil.getNombreComercial();
    }
    if (perfil.getNombreCompleto() != null && !perfil.getNombreCompleto().isBlank()) {
      return perfil.getNombreCompleto();
    }
    return null;
  }

  private Instant fechaRegistro(PerfilUsuario perfil) {
    return perfil.getFechaCreacion().toInstant(ZoneOffset.UTC);
  }
}
