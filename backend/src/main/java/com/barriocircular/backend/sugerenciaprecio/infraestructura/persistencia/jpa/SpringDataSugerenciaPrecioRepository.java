package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSugerenciaPrecioRepository
    extends JpaRepository<SugerenciaPrecioEntity, UUID> {

  List<SugerenciaPrecioEntity> findByTipoMaterialOrderByFechaSugerenciaDesc(String tipoMaterial);
}
