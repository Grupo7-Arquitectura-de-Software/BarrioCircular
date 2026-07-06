package com.barriocircular.backend.publicacion.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa.PublicacionEntity;
import com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa.SpringDataPublicacionRepository;
import com.barriocircular.backend.publicacion.infraestructura.persistencia.mapeadores.PublicacionMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PublicacionRepositorioAdapter implements PublicacionRepositorio {

  private final SpringDataPublicacionRepository springDataRepository;
  private final PublicacionMapper mapper;

  public PublicacionRepositorioAdapter(
      SpringDataPublicacionRepository springDataRepository, PublicacionMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public Publicacion guardar(Publicacion publicacion) {
    PublicacionEntity guardada = springDataRepository.save(mapper.toEntity(publicacion));
    return mapper.toDomain(guardada);
  }

  @Override
  public Optional<Publicacion> buscarPorId(PublicacionId id) {
    return springDataRepository.findById(id.valor()).map(mapper::toDomain);
  }

  @Override
  public boolean existePorId(PublicacionId id) {
    return springDataRepository.existsById(id.valor());
  }

  @Override
  public List<Publicacion> listarPorEstado(EstadoPublicacion estado) {
    return springDataRepository.findByEstadoOrderByFechaCreacionDesc(estado.name()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Publicacion> listarPorCreador(CiudadanoId creadorId) {
    return springDataRepository.findByCreadorIdOrderByFechaCreacionDesc(creadorId.valor()).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<Publicacion> listarPorReservador(ReservadorId reservadorId) {
    return springDataRepository
        .findByReservadoPorOrderByFechaCreacionDesc(reservadorId.valor())
        .stream()
        .map(mapper::toDomain)
        .toList();
  }
}
