package com.barriocircular.backend.logistica.infraestructura.integracion.publicacion;

import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.ListarMisReservasUseCase;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ReservasCatalogoAdapter implements ReservasCatalogoPort {

  private static final String ESTADO_RESERVADA = "RESERVADA";

  private final ListarMisReservasUseCase listarMisReservasUseCase;
  private final PerfilUsuarioRepository perfilUsuarioRepository;
  private final CuentaAccesoRepositorio cuentaAccesoRepositorio;

  public ReservasCatalogoAdapter(
      ListarMisReservasUseCase listarMisReservasUseCase,
      PerfilUsuarioRepository perfilUsuarioRepository,
      CuentaAccesoRepositorio cuentaAccesoRepositorio) {
    this.listarMisReservasUseCase =
        Objects.requireNonNull(
            listarMisReservasUseCase, "El caso de uso de reservas es obligatorio.");
    this.perfilUsuarioRepository =
        Objects.requireNonNull(
            perfilUsuarioRepository, "El repositorio de perfiles es obligatorio.");
    this.cuentaAccesoRepositorio =
        Objects.requireNonNull(cuentaAccesoRepositorio, "El repositorio de acceso es obligatorio.");
  }

  @Override
  public List<ReservaCatalogo> obtenerReservasActivasPorReciclador(UUID recicladorId) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");

    return obtenerClerkIdDelReciclador(recicladorId).stream()
        .flatMap(clerkId -> listarMisReservasUseCase.ejecutar(clerkId).stream())
        .filter(publicacion -> esReservaActivaDelReciclador(publicacion, recicladorId))
        .map(this::traducirReserva)
        .toList();
  }

  private java.util.Optional<String> obtenerClerkIdDelReciclador(UUID recicladorId) {
    return perfilUsuarioRepository
        .buscarPorId(recicladorId)
        .filter(this::esRecicladorActivo)
        .flatMap(perfil -> cuentaAccesoRepositorio.buscarPorId(perfil.getCuentaUsuarioId()))
        .map(cuenta -> cuenta.getClerkId().valor());
  }

  private boolean esRecicladorActivo(PerfilUsuario perfil) {
    return perfil.getRol() == RolUsuario.RECICLADOR
        && perfil.getEstadoPerfil() == EstadoPerfil.ACTIVO;
  }

  private boolean esReservaActivaDelReciclador(
      PublicacionResultado publicacion, UUID recicladorId) {
    return ESTADO_RESERVADA.equals(publicacion.estado())
        && recicladorId.equals(publicacion.reservadoPor());
  }

  private ReservaCatalogo traducirReserva(PublicacionResultado publicacion) {
    return new ReservaCatalogo(
        publicacion.publicacionId(),
        publicacion.creadorId(),
        publicacion.tipoResiduo(),
        publicacion.pesoKg(),
        publicacion.latitud(),
        publicacion.longitud(),
        publicacion.fechaCreacion());
  }
}
