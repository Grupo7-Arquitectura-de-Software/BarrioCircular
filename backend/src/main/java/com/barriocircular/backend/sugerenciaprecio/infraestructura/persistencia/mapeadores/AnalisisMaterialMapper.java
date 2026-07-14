package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterialId;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.ResultadoAnalisis;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.AnalisisMaterialEntity;
import org.springframework.stereotype.Component;

@Component
public class AnalisisMaterialMapper {

  public AnalisisMaterialEntity toEntity(AnalisisMaterial analisis) {
    return new AnalisisMaterialEntity(
        analisis.id().valor(),
        analisis.resultado().name(),
        analisis.tipoMaterial() == null ? null : analisis.tipoMaterial().name(),
        analisis.pesoEstimadoKg(),
        analisis.estadoMaterial() == null ? null : analisis.estadoMaterial().name(),
        analisis.precioSugerido() == null ? null : analisis.precioSugerido().valor(),
        analisis.recomendacion(),
        analisis.solicitanteClerkId(),
        analisis.fechaAnalisis());
  }

  public AnalisisMaterial toDomain(AnalisisMaterialEntity entity) {
    return AnalisisMaterial.reconstituir(
        AnalisisMaterialId.de(entity.getId()),
        ResultadoAnalisis.valueOf(entity.getResultado()),
        entity.getTipoMaterial() == null
            ? null
            : TipoMaterialSugerido.valueOf(entity.getTipoMaterial()),
        entity.getPesoEstimadoKg(),
        entity.getEstadoMaterial() == null
            ? null
            : EstadoMaterial.valueOf(entity.getEstadoMaterial()),
        entity.getPrecioSugerido() == null ? null : new PrecioSugerido(entity.getPrecioSugerido()),
        entity.getRecomendacion(),
        entity.getSolicitanteClerkId(),
        entity.getFechaAnalisis());
  }
}
