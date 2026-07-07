package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import com.barriocircular.backend.perfiles.aplicacion.comandos.ActualizarMiPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.mapeadores.PerfilResultadoMapper;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.dominio.eventos.EventoDominio;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarMiPerfilUseCase {

  private final CuentaAccesoConsultor cuentaAccesoConsultor;
  private final PerfilUsuarioRepository perfilUsuarioRepository;
  private final ApplicationEventPublisher publicadorEventos;

  public ActualizarMiPerfilUseCase(
      CuentaAccesoConsultor cuentaAccesoConsultor,
      PerfilUsuarioRepository perfilUsuarioRepository,
      ApplicationEventPublisher publicadorEventos) {
    this.cuentaAccesoConsultor = cuentaAccesoConsultor;
    this.perfilUsuarioRepository = perfilUsuarioRepository;
    this.publicadorEventos = publicadorEventos;
  }

  @Transactional
  public PerfilResultado ejecutar(ActualizarMiPerfilCommand comando) {
    UUID cuentaUsuarioId =
        cuentaAccesoConsultor
            .obtenerCuentaIdPorClerkId(comando.clerkIdAutenticado())
            .orElseThrow(CuentaAccesoNoEncontradaException::new);

    PerfilUsuario perfilUsuario =
        perfilUsuarioRepository
            .buscarPorCuentaUsuarioId(cuentaUsuarioId)
            .orElseThrow(PerfilNoEncontradoException::new);

    actualizarNombreSiCorresponde(perfilUsuario, comando);
    actualizarTelefonoSiCorresponde(perfilUsuario, comando);
    actualizarUbicacionSiCorresponde(perfilUsuario, comando);
    actualizarDireccionSiCorresponde(perfilUsuario, comando);

    perfilUsuarioRepository.guardar(perfilUsuario);
    publicarEventos(perfilUsuario);

    return PerfilResultadoMapper.desde(perfilUsuario);
  }

  private void actualizarNombreSiCorresponde(
      PerfilUsuario perfilUsuario, ActualizarMiPerfilCommand comando) {
    if (comando.nombre() == null && comando.apellido() == null) {
      return;
    }

    String nombreActual = perfilUsuario.getNombreCompleto();
    String nombre = comando.nombre() != null ? comando.nombre() : primerSegmento(nombreActual);
    String apellido =
        comando.apellido() != null ? comando.apellido() : segmentosRestantes(nombreActual);
    perfilUsuario.actualizarNombreCompleto(unirNombreApellido(nombre, apellido));
  }

  private void actualizarTelefonoSiCorresponde(
      PerfilUsuario perfilUsuario, ActualizarMiPerfilCommand comando) {
    if (comando.telefono() == null) {
      return;
    }

    perfilUsuario.actualizarInformacionContacto(
        new InformacionContacto(
            perfilUsuario.getInformacionContacto().getCorreoElectronico(), comando.telefono()));
  }

  private void actualizarUbicacionSiCorresponde(
      PerfilUsuario perfilUsuario, ActualizarMiPerfilCommand comando) {
    if (comando.latitud() == null && comando.longitud() == null) {
      return;
    }

    double latitud =
        comando.latitud() != null
            ? comando.latitud()
            : perfilUsuario.getUbicacionHabitual().getLatitud();
    double longitud =
        comando.longitud() != null
            ? comando.longitud()
            : perfilUsuario.getUbicacionHabitual().getLongitud();
    perfilUsuario.actualizarUbicacionHabitual(new CoordenadaGPS(latitud, longitud));
  }

  private void actualizarDireccionSiCorresponde(
      PerfilUsuario perfilUsuario, ActualizarMiPerfilCommand comando) {
    if (comando.direccion() != null) {
      perfilUsuario.actualizarDireccionHabitual(comando.direccion());
    }
  }

  private void publicarEventos(PerfilUsuario perfilUsuario) {
    for (EventoDominio eventoDominio : perfilUsuario.obtenerEventosDominio()) {
      publicadorEventos.publishEvent(eventoDominio);
    }
    perfilUsuario.limpiarEventosDominio();
  }

  private String primerSegmento(String nombreCompleto) {
    if (nombreCompleto == null || nombreCompleto.isBlank()) {
      return "";
    }
    return nombreCompleto.trim().split("\\s+", 2)[0];
  }

  private String segmentosRestantes(String nombreCompleto) {
    if (nombreCompleto == null || nombreCompleto.isBlank()) {
      return "";
    }
    String[] partes = nombreCompleto.trim().split("\\s+", 2);
    return partes.length == 2 ? partes[1] : "";
  }

  private String unirNombreApellido(String nombre, String apellido) {
    String nombreNormalizado = nombre == null ? "" : nombre.trim();
    String apellidoNormalizado = apellido == null ? "" : apellido.trim();
    return (nombreNormalizado + " " + apellidoNormalizado).trim();
  }
}
