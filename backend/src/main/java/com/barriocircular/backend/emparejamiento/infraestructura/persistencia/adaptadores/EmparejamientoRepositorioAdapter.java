package com.barriocircular.backend.emparejamiento.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.emparejamiento.dominio.modelo.agregado.ResultadoEmparejamiento;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CompradorId;
import com.barriocircular.backend.emparejamiento.dominio.repositorios.EmparejamientoRepositorio;
import com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa.ResultadoEmparejamientoEntity;
import com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa.SpringDataEmparejamientoRepository;
import com.barriocircular.backend.emparejamiento.infraestructura.persistencia.mapeadores.ResultadoEmparejamientoMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class EmparejamientoRepositorioAdapter implements EmparejamientoRepositorio {

  private final SpringDataEmparejamientoRepository springDataRepository;
  private final ResultadoEmparejamientoMapper mapper;

  public EmparejamientoRepositorioAdapter(
      SpringDataEmparejamientoRepository springDataRepository,
      ResultadoEmparejamientoMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public ResultadoEmparejamiento guardar(ResultadoEmparejamiento resultado) {
    ResultadoEmparejamientoEntity guardado = springDataRepository.save(mapper.toEntity(resultado));
    return mapper.toDomain(guardado);
  }

  @Override
  public Optional<ResultadoEmparejamiento> buscarPorId(UUID id) {
    return springDataRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<ResultadoEmparejamiento> listarPorComprador(CompradorId compradorId) {
    return springDataRepository.findByCompradorIdOrderByFechaCalculoDesc(compradorId.valor()).stream()
        .map(mapper::toDomain)
        .toList();
  }
}
