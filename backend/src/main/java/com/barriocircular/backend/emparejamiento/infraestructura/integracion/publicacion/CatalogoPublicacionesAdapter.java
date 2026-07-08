package com.barriocircular.backend.emparejamiento.infraestructura.integracion.publicacion;

import com.barriocircular.backend.emparejamiento.aplicacion.puertos.CatalogoPublicacionesPort;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.TipoMaterialFiltro;
import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.ListarPublicacionesDisponiblesUseCase;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CatalogoPublicacionesAdapter implements CatalogoPublicacionesPort {

  private final ListarPublicacionesDisponiblesUseCase listarPublicacionesDisponiblesUseCase;

  public CatalogoPublicacionesAdapter(
      ListarPublicacionesDisponiblesUseCase listarPublicacionesDisponiblesUseCase) {
    this.listarPublicacionesDisponiblesUseCase = listarPublicacionesDisponiblesUseCase;
  }

  @Override
  public List<OfertaCatalogo> obtenerCatalogoDisponible() {
    return listarPublicacionesDisponiblesUseCase.ejecutar().stream()
        .map(this::traducirDesdePublishedLanguage)
        .toList();
  }

  private OfertaCatalogo traducirDesdePublishedLanguage(PublicacionResultado publicado) {
    return new OfertaCatalogo(
        publicado.publicacionId(),
        TipoMaterialFiltro.valueOf(publicado.tipoResiduo()),
        publicado.pesoKg(),
        publicado.precioPorKilo(),
        publicado.latitud(),
        publicado.longitud(),
        publicado.estado());
  }
}
