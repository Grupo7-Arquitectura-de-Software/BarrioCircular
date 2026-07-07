package com.barriocircular.backend.perfiles.dominio.repositorios;

import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import java.util.Optional;
import java.util.UUID;

public interface PerfilUsuarioRepository {

  void guardar(PerfilUsuario perfil);

  Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId);

  boolean existePorDocumentoIdentificacion(DocumentoIdentificacion documentoIdentificacion);

  boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId);
}
