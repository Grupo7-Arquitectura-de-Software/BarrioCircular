package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.FuenteSugerencia;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.PrecioSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecioId;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa.SugerenciaPrecioEntity;
import org.springframework.stereotype.Component;

@Component
public class SugerenciaPrecioMapper {

  public SugerenciaPrecioEntity toEntity(SugerenciaPrecio sugerenciaPrecio) {
    return new SugerenciaPrecioEntity(
        sugerenciaPrecio.id().valor(),
        sugerenciaPrecio.tipoMaterial().name(),
        sugerenciaPrecio.pesoKg(),
        sugerenciaPrecio.precioSugerido().valor(),
        sugerenciaPrecio.fuente().name(),
        sugerenciaPrecio.justificacion(),
        sugerenciaPrecio.solicitanteClerkId(),
        sugerenciaPrecio.fechaSugerencia());
  }

  public SugerenciaPrecio toDomain(SugerenciaPrecioEntity entity) {
    return SugerenciaPrecio.reconstituir(
        SugerenciaPrecioId.de(entity.getId()),
        TipoMaterialSugerido.valueOf(entity.getTipoMaterial()),
        entity.getPesoKg(),
        new PrecioSugerido(entity.getPrecioSugerido()),
        FuenteSugerencia.valueOf(entity.getFuente()),
        entity.getJustificacion(),
        entity.getSolicitanteClerkId(),
        entity.getFechaSugerencia());
  }
}
