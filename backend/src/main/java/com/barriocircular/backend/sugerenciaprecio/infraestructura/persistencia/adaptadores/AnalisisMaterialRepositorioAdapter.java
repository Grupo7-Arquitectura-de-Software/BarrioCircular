package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.AnalisisMaterialRepositorio;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.AnalisisMaterialEntity;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.SpringDataAnalisisMaterialRepository;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.mapeadores.AnalisisMaterialMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AnalisisMaterialRepositorioAdapter implements AnalisisMaterialRepositorio {

  private final SpringDataAnalisisMaterialRepository springDataRepository;
  private final AnalisisMaterialMapper mapper;

  public AnalisisMaterialRepositorioAdapter(
      SpringDataAnalisisMaterialRepository springDataRepository, AnalisisMaterialMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public AnalisisMaterial guardar(AnalisisMaterial analisisMaterial) {
    AnalisisMaterialEntity guardado = springDataRepository.save(mapper.toEntity(analisisMaterial));
    return mapper.toDomain(guardado);
  }
}
