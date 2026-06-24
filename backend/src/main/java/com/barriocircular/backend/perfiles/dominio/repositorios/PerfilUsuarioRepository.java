package com.barriocircular.backend.perfiles.dominio.repositorios;

import java.util.Optional;
import java.util.UUID;

import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;

public interface PerfilUsuarioRepository {

    void guardar(PerfilUsuario perfil);

    Optional<PerfilUsuario> buscarPorId(UUID id);

    Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId);

    boolean existePorDocumentoIdentificacion(DocumentoIdentificacion documentoIdentificacion);

    boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId);
}
