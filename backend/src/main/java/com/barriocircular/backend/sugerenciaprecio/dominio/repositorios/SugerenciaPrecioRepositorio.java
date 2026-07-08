package com.barriocircular.backend.sugerenciaprecio.dominio.repositorios;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecioId;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import java.util.List;
import java.util.Optional;

public interface SugerenciaPrecioRepositorio {

  SugerenciaPrecio guardar(SugerenciaPrecio sugerenciaPrecio);

  Optional<SugerenciaPrecio> buscarPorId(SugerenciaPrecioId id);

  List<SugerenciaPrecio> listarPorTipoMaterial(TipoMaterialSugerido tipoMaterial);
}
