package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import com.barriocircular.backend.perfiles.aplicacion.comandos.ActualizarDatosPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.dominio.eventos.EventoDominio;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarDatosPerfilUseCase {

  private final PerfilUsuarioRepository perfilUsuarioRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ActualizarDatosPerfilUseCase(
      PerfilUsuarioRepository perfilUsuarioRepository, ApplicationEventPublisher eventPublisher) {
    this.perfilUsuarioRepository = perfilUsuarioRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public PerfilResultado ejecutar(ActualizarDatosPerfilCommand command) {
    PerfilUsuario perfil =
        perfilUsuarioRepository
            .buscarPorId(command.perfilId())
            .orElseThrow(
                () ->
                    new PerfilDomainException("No existe un perfil con id " + command.perfilId()));

    if (command.correoElectronico() != null || command.telefono() != null) {
      String correo =
          command.correoElectronico() != null
              ? command.correoElectronico()
              : perfil.getInformacionContacto().getCorreoElectronico();
      String telefono =
          command.telefono() != null
              ? command.telefono()
              : perfil.getInformacionContacto().getTelefono();
      perfil.actualizarInformacionContacto(new InformacionContacto(correo, telefono));
    }

    if (command.latitud() != null || command.longitud() != null) {
      double latitud =
          command.latitud() != null
              ? command.latitud()
              : perfil.getUbicacionHabitual().getLatitud();
      double longitud =
          command.longitud() != null
              ? command.longitud()
              : perfil.getUbicacionHabitual().getLongitud();
      perfil.actualizarUbicacionHabitual(new CoordenadaGPS(latitud, longitud));
    }

    if (command.rol() != null && !command.rol().isBlank()) {
      perfil.cambiarRol(RolUsuario.valueOf(command.rol()));
    }

    perfilUsuarioRepository.guardar(perfil);
    publicarEventos(perfil);

    return convertirResultado(perfil);
  }

  private void publicarEventos(PerfilUsuario perfil) {
    for (EventoDominio evento : perfil.obtenerEventosDominio()) {
      eventPublisher.publishEvent(evento);
    }
    perfil.limpiarEventosDominio();
  }

  private PerfilResultado convertirResultado(PerfilUsuario perfil) {
    return new PerfilResultado(
        perfil.getId(),
        perfil.getCuentaUsuarioId(),
        perfil.getDocumentoIdentificacion().getValor(),
        perfil.getNombreCompleto(),
        perfil.getNombreComercial(),
        perfil.getRol().name(),
        perfil.getEstadoPerfil().name(),
        perfil.getInformacionContacto().getCorreoElectronico(),
        perfil.getInformacionContacto().getTelefono(),
        perfil.getUbicacionHabitual().getLatitud(),
        perfil.getUbicacionHabitual().getLongitud());
  }
}
