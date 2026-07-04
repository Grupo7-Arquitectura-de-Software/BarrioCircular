package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilYaExisteException;
import com.barriocircular.backend.perfiles.aplicacion.mapeadores.PerfilResultadoMapper;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.aplicacion.puertos.PerfilOnboardingPendienteRepository;
import com.barriocircular.backend.perfiles.dominio.eventos.EventoDominio;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearPerfilUseCase {

  private final PerfilUsuarioRepository perfilUsuarioRepository;
  private final PerfilOnboardingPendienteRepository onboardingPendienteRepository;
  private final ApplicationEventPublisher publicadorEventos;
  private final CuentaAccesoConsultor cuentaAccesoConsultor;

  public CrearPerfilUseCase(
          PerfilUsuarioRepository perfilUsuarioRepository,
          PerfilOnboardingPendienteRepository onboardingPendienteRepository,
          ApplicationEventPublisher publicadorEventos,
          CuentaAccesoConsultor cuentaAccesoConsultor) {
    this.perfilUsuarioRepository = perfilUsuarioRepository;
    this.onboardingPendienteRepository = onboardingPendienteRepository;
    this.publicadorEventos = publicadorEventos;
    this.cuentaAccesoConsultor = cuentaAccesoConsultor;
  }

  @Transactional
  public PerfilResultado ejecutar(CrearPerfilCommand comando, String clerkIdAutenticado) {
    UUID cuentaIdDerivada = cuentaAccesoConsultor.obtenerCuentaIdPorClerkId(clerkIdAutenticado)
            .orElseThrow(CuentaAccesoNoEncontradaException::new);

    if (perfilUsuarioRepository.existePorCuentaUsuarioId(cuentaIdDerivada)) {
      throw new PerfilYaExisteException("La cuenta ya tiene un perfil de usuario registrado.");
    }

    DocumentoIdentificacion documentoIdentificacion =
        new DocumentoIdentificacion(comando.documentoIdentificacion());
    if (perfilUsuarioRepository.existePorDocumentoIdentificacion(documentoIdentificacion)) {
      throw new PerfilYaExisteException(
          "El documento de identificación ya pertenece a otro perfil.");
    }

    InformacionContacto informacionContacto =
        new InformacionContacto(comando.correoElectronico(), comando.telefono());
    CoordenadaGPS ubicacionHabitual = new CoordenadaGPS(comando.latitud(), comando.longitud());
    RolUsuario rolUsuario = RolUsuario.valueOf(comando.rol());

    PerfilUsuario perfilUsuario =
        PerfilUsuarioFactory.crearPerfil(
            cuentaIdDerivada,
            documentoIdentificacion,
            comando.nombreCompleto(),
            comando.nombreComercial(),
            rolUsuario,
            informacionContacto,
            ubicacionHabitual);

    perfilUsuarioRepository.guardar(perfilUsuario);
    onboardingPendienteRepository.eliminarPorCuentaId(cuentaIdDerivada);
    publicarEventos(perfilUsuario);

    return PerfilResultadoMapper.desde(perfilUsuario);
  }

  private void publicarEventos(PerfilUsuario perfilUsuario) {
    for (EventoDominio eventoDominio : perfilUsuario.obtenerEventosDominio()) {
      publicadorEventos.publishEvent(eventoDominio);
    }
    perfilUsuario.limpiarEventosDominio();
  }
}
