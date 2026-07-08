package com.barriocircular.backend.emparejamiento.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.emparejamiento.dominio.modelo.agregado.ResultadoEmparejamiento;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CompradorId;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PreferenciaFiltro;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PuntajeOferta;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.TipoMaterialFiltro;
import com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa.PuntajeOfertaEmbeddable;
import com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa.ResultadoEmparejamientoEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ResultadoEmparejamientoMapper {

  public ResultadoEmparejamientoEntity toEntity(ResultadoEmparejamiento resultado) {
    Set<String> tiposMaterialFiltro =
        resultado.filtroAplicado().tiposMaterial().stream()
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    List<PuntajeOfertaEmbeddable> ofertasOrdenadas =
        resultado.ofertasOrdenadas().stream()
            .map(
                puntaje ->
                    new PuntajeOfertaEmbeddable(
                        puntaje.publicacionId(),
                        puntaje.distanciaKm(),
                        puntaje.precioKg(),
                        puntaje.scoreTotal()))
            .toList();

    return new ResultadoEmparejamientoEntity(
        resultado.id(),
        resultado.compradorId().valor(),
        resultado.posicionOrigen().latitud(),
        resultado.posicionOrigen().longitud(),
        tiposMaterialFiltro,
        resultado.filtroAplicado().radioMaximoKm(),
        resultado.filtroAplicado().zonaDescriptiva(),
        ofertasOrdenadas,
        resultado.fechaCalculo());
  }

  public ResultadoEmparejamiento toDomain(ResultadoEmparejamientoEntity entity) {
    Set<TipoMaterialFiltro> tiposMaterial =
        entity.getTiposMaterialFiltro().stream()
            .map(TipoMaterialFiltro::valueOf)
            .collect(Collectors.toUnmodifiableSet());

    PreferenciaFiltro filtro =
        new PreferenciaFiltro(
            tiposMaterial, entity.getRadioMaximoKm(), entity.getZonaDescriptiva(), null, null);

    List<PuntajeOferta> ofertasOrdenadas =
        entity.getOfertasOrdenadas().stream()
            .map(
                embeddable ->
                    new PuntajeOferta(
                        embeddable.getPublicacionId(),
                        embeddable.getDistanciaKm(),
                        embeddable.getPrecioKg(),
                        embeddable.getScoreTotal()))
            .toList();

    return ResultadoEmparejamiento.reconstituir(
        entity.getId(),
        CompradorId.de(entity.getCompradorId()),
        new CoordenadaGPS(entity.getLatitudOrigen(), entity.getLongitudOrigen()),
        filtro,
        ofertasOrdenadas,
        entity.getFechaCalculo());
  }
}
