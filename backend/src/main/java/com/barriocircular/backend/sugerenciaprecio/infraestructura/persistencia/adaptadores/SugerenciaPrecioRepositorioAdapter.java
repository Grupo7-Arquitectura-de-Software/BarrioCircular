package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecioId;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.SugerenciaPrecioRepositorio;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.SpringDataSugerenciaPrecioRepository;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.SugerenciaPrecioEntity;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.mapeadores.SugerenciaPrecioMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SugerenciaPrecioRepositorioAdapter implements SugerenciaPrecioRepositorio {

  private final SpringDataSugerenciaPrecioRepository springDataRepository;
  private final SugerenciaPrecioMapper mapper;

  public SugerenciaPrecioRepositorioAdapter(
      SpringDataSugerenciaPrecioRepository springDataRepository, SugerenciaPrecioMapper mapper) {
    this.springDataRepository = springDataRepository;
    this.mapper = mapper;
  }

  @Override
  public SugerenciaPrecio guardar(SugerenciaPrecio sugerenciaPrecio) {
    SugerenciaPrecioEntity guardada = springDataRepository.save(mapper.toEntity(sugerenciaPrecio));
    return mapper.toDomain(guardada);
  }

  @Override
  public Optional<SugerenciaPrecio> buscarPorId(SugerenciaPrecioId id) {
    return springDataRepository.findById(id.valor()).map(mapper::toDomain);
  }

  @Override
  public List<SugerenciaPrecio> listarPorTipoMaterial(TipoMaterialSugerido tipoMaterial) {
    return springDataRepository
        .findByTipoMaterialOrderByFechaSugerenciaDesc(tipoMaterial.name())
        .stream()
        .map(mapper::toDomain)
        .toList();
  }
}
