package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilCapacidadResult;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;

@Service
public class ConsultarCapacidadesPerfilUseCase {

    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public ConsultarCapacidadesPerfilUseCase(PerfilUsuarioRepository perfilUsuarioRepository) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public PerfilCapacidadResult ejecutar(UUID perfilId) {
        PerfilUsuario perfil = perfilUsuarioRepository.buscarPorId(perfilId)
                .orElseThrow(PerfilNoEncontradoException::new);

        return new PerfilCapacidadResult(
                perfil.getId(),
                perfil.getCuentaUsuarioId(),
                perfil.getRol().name(),
                perfil.getEstadoPerfil().name(),
                perfil.puedePublicarMateriales(),
                perfil.puedeComprarMateriales());
    }
}
