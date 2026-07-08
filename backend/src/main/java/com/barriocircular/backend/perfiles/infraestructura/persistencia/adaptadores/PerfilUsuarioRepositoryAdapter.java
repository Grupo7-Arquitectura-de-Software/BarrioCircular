package com.barriocircular.backend.perfiles.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.mapeadores.PerfilUsuarioMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilUsuarioRepositoryAdapter implements PerfilUsuarioRepository {

  private final SpringDataPerfilUsuarioRepository springDataRepository;
  private final PerfilUsuarioMapper mapper;

  public PerfilUsuarioRepositoryAdapter(
      SpringDataPerfilUsuarioRepository springDataRepository, PerfilUsuarioMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public void guardar(PerfilUsuario perfil) {
    springDataRepository.save(mapper.toEntity(perfil));
  }

  @Override
  public Optional<PerfilUsuario> buscarPorId(UUID id) {
    return springDataRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId) {
    return springDataRepository.findByCuentaUsuarioId(cuentaUsuarioId).map(mapper::toDomain);
  }

  @Override
  public boolean existePorDocumentoIdentificacion(DocumentoIdentificacion documentoIdentificacion) {
    return springDataRepository.existsByDocumentoIdentificacion(documentoIdentificacion.getValor());
  }

  @Override
  public boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId) {
    return springDataRepository.existsByCuentaUsuarioId(cuentaUsuarioId);
  }
}
