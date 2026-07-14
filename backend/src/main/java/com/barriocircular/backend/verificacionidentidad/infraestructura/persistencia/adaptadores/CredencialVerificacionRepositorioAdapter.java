package com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.EstadoCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.repositorios.CredencialVerificacionRepositorio;
import com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.jpa.CredencialVerificacionEntity;
import com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.jpa.SpringDataCredencialVerificacionRepository;
import com.barriocircular.backend.verificacionidentidad.infraestructura.persistencia.mapeadores.CredencialVerificacionMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CredencialVerificacionRepositorioAdapter implements CredencialVerificacionRepositorio {

  private final SpringDataCredencialVerificacionRepository springDataRepository;
  private final CredencialVerificacionMapper mapper;

  public CredencialVerificacionRepositorioAdapter(
      SpringDataCredencialVerificacionRepository springDataRepository,
      CredencialVerificacionMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public CredencialVerificacion guardar(CredencialVerificacion credencial) {
    CredencialVerificacionEntity guardada = springDataRepository.save(mapper.toEntity(credencial));
    return mapper.toDomain(guardada);
  }

  @Override
  public Optional<CredencialVerificacion> buscarPorToken(TokenVerificacion token) {
    return springDataRepository.findByToken(token.valor()).map(mapper::toDomain);
  }

  @Override
  public Optional<CredencialVerificacion> buscarActivaPorPerfil(UUID perfilId) {
    return springDataRepository
        .findFirstByPerfilIdAndEstadoOrderByFechaEmisionDesc(
            perfilId, EstadoCredencial.ACTIVA.name())
        .map(mapper::toDomain);
  }

  @Override
  public List<CredencialVerificacion> listarPorPerfil(UUID perfilId) {
    return springDataRepository.findByPerfilIdOrderByFechaEmisionDesc(perfilId).stream()
        .map(mapper::toDomain)
        .toList();
  }
}
