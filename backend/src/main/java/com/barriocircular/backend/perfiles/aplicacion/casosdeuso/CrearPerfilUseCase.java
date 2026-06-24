package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.dominio.eventos.EventoDominio;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;

@Service
public class CrearPerfilUseCase {

    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CrearPerfilUseCase(
            PerfilUsuarioRepository perfilUsuarioRepository,
            ApplicationEventPublisher eventPublisher) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PerfilResultado ejecutar(CrearPerfilCommand command) {
        DocumentoIdentificacion documento = new DocumentoIdentificacion(command.documentoIdentificacion());
        InformacionContacto contacto = new InformacionContacto(command.correoElectronico(), command.telefono());
        CoordenadaGPS ubicacion = new CoordenadaGPS(command.latitud(), command.longitud());
        RolUsuario rol = RolUsuario.valueOf(command.rol());

        PerfilUsuario perfil = PerfilUsuarioFactory.crearPerfil(command.cuentaUsuarioId(), documento,
                command.nombreCompleto(), command.nombreComercial(), rol, contacto, ubicacion);

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
