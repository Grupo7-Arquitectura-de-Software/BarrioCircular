package com.barriocircular.backend.emparejamiento.aplicacion.casosdeuso;

import com.barriocircular.backend.emparejamiento.aplicacion.comandos.BuscarOfertasOptimasCommand;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.OfertaRecomendadaResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.PerfilCapacidadesComprador;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.ResultadoEmparejamientoResultado;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.CatalogoPublicacionesNoDisponibleException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoAutorizadoException;
import com.barriocircular.backend.emparejamiento.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.emparejamiento.aplicacion.puertos.CatalogoPublicacionesPort;
import com.barriocircular.backend.emparejamiento.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.emparejamiento.dominio.eventos.EventoDominio;
import com.barriocircular.backend.emparejamiento.dominio.modelo.agregado.ResultadoEmparejamiento;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CompradorId;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PreferenciaFiltro;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.PuntajeOferta;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.TipoMaterialFiltro;
import com.barriocircular.backend.emparejamiento.dominio.repositorios.EmparejamientoRepositorio;
import com.barriocircular.backend.emparejamiento.dominio.servicios.AlgoritmoEmparejamientoGeografico;
import com.barriocircular.backend.emparejamiento.dominio.servicios.CalculadorDistanciaGeografica;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalcularOfertasOptimasUseCase {

  private final CatalogoPublicacionesPort catalogoPublicacionesPort;
  private final PerfilConsultor perfilConsultor;
  private final EmparejamientoRepositorio emparejamientoRepositorio;
  private final ApplicationEventPublisher eventPublisher;
  private final AlgoritmoEmparejamientoGeografico algoritmo;

  public CalcularOfertasOptimasUseCase(
      CatalogoPublicacionesPort catalogoPublicacionesPort,
      PerfilConsultor perfilConsultor,
      EmparejamientoRepositorio emparejamientoRepositorio,
      ApplicationEventPublisher eventPublisher) {
    this.catalogoPublicacionesPort = catalogoPublicacionesPort;
    this.perfilConsultor = perfilConsultor;
    this.emparejamientoRepositorio = emparejamientoRepositorio;
    this.eventPublisher = eventPublisher;
    this.algoritmo = new AlgoritmoEmparejamientoGeografico(new CalculadorDistanciaGeografica());
  }

  @Transactional
  public ResultadoEmparejamientoResultado ejecutar(
      BuscarOfertasOptimasCommand comando, String clerkIdAutenticado) {
    PerfilCapacidadesComprador perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    if (!perfil.puedeComprarMateriales()) {
      throw new PerfilNoAutorizadoException(
          "El perfil autenticado no esta autorizado para buscar materiales.");
    }

    CompradorId compradorId = CompradorId.de(perfil.perfilId());
    CoordenadaGPS posicionOrigen = new CoordenadaGPS(comando.latitud(), comando.longitud());
    Set<TipoMaterialFiltro> tiposMaterial = convertirTipos(comando.tiposMaterial());
    PreferenciaFiltro filtro =
        new PreferenciaFiltro(
            tiposMaterial,
            comando.radioMaximoKm(),
            comando.zonaDescriptiva(),
            comando.pesoMinimo(),
            comando.pesoMaximo());

    List<OfertaCatalogo> catalogoDisponible = obtenerCatalogoDisponible();

    List<PuntajeOferta> ofertasOrdenadas =
        algoritmo.calcularOfertasOptimas(posicionOrigen, filtro, catalogoDisponible);

    ResultadoEmparejamiento resultado =
        ResultadoEmparejamiento.calcular(compradorId, posicionOrigen, filtro, ofertasOrdenadas);

    ResultadoEmparejamiento guardado = emparejamientoRepositorio.guardar(resultado);
    publicarEventos(resultado);

    return convertirResultado(guardado);
  }

  private List<OfertaCatalogo> obtenerCatalogoDisponible() {
    try {
      return catalogoPublicacionesPort.obtenerCatalogoDisponible();
    } catch (RuntimeException causa) {
      throw new CatalogoPublicacionesNoDisponibleException(
          "No fue posible obtener el catalogo de publicaciones disponibles desde "
              + "Publicacion de Materiales.",
          causa);
    }
  }

  private Set<TipoMaterialFiltro> convertirTipos(Set<String> tiposMaterial) {
    if (tiposMaterial == null) {
      return Set.of();
    }
    return tiposMaterial.stream()
        .map(TipoMaterialFiltro::valueOf)
        .collect(Collectors.toUnmodifiableSet());
  }

  private void publicarEventos(ResultadoEmparejamiento resultado) {
    for (EventoDominio evento : resultado.eventos()) {
      eventPublisher.publishEvent(evento);
    }
    resultado.limpiarEventos();
  }

  private ResultadoEmparejamientoResultado convertirResultado(ResultadoEmparejamiento resultado) {
    List<OfertaRecomendadaResultado> ofertas =
        resultado.ofertasOrdenadas().stream()
            .map(
                puntaje ->
                    new OfertaRecomendadaResultado(
                        puntaje.publicacionId(),
                        puntaje.distanciaKm(),
                        puntaje.precioKg(),
                        puntaje.scoreTotal()))
            .toList();

    return new ResultadoEmparejamientoResultado(
        resultado.id(),
        resultado.compradorId().valor(),
        resultado.posicionOrigen().latitud(),
        resultado.posicionOrigen().longitud(),
        resultado.filtroAplicado().radioMaximoKm(),
        resultado.fechaCalculo(),
        ofertas);
  }
}
