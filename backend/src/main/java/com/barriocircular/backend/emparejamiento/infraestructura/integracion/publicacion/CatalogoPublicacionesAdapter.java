package com.barriocircular.backend.emparejamiento.infraestructura.integracion.publicacion;

import com.barriocircular.backend.emparejamiento.aplicacion.puertos.CatalogoPublicacionesPort;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.OfertaCatalogo;
import com.barriocircular.backend.emparejamiento.dominio.modelo.objetosValor.TipoMaterialFiltro;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.publicacion.aplicacion.casosdeuso.ListarPublicacionesDisponiblesUseCase;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CatalogoPublicacionesAdapter implements CatalogoPublicacionesPort {

  private final ListarPublicacionesDisponiblesUseCase listarPublicacionesDisponiblesUseCase;
  private final PerfilUsuarioRepository repositorioPerfiles;

  public CatalogoPublicacionesAdapter(
      ListarPublicacionesDisponiblesUseCase listarPublicacionesDisponiblesUseCase,
      PerfilUsuarioRepository repositorioPerfiles) {
    this.listarPublicacionesDisponiblesUseCase = listarPublicacionesDisponiblesUseCase;
    this.repositorioPerfiles = repositorioPerfiles;
  }

  @Override
  public List<OfertaCatalogo> obtenerCatalogoDisponible() {
    List<PublicacionResultado> publicaciones = listarPublicacionesDisponiblesUseCase.ejecutar();
    Map<UUID, String> rolesCache = new HashMap<>();

    return publicaciones.stream()
        .map(
            pub -> {
              String creadorRol =
                  rolesCache.computeIfAbsent(
                      pub.creadorId(),
                      id ->
                          repositorioPerfiles
                              .buscarPorId(id)
                              .map(p -> p.getRol().name())
                              .orElse(null));
              return traducirDesdePublishedLanguage(pub, creadorRol);
            })
        .toList();
  }

  private OfertaCatalogo traducirDesdePublishedLanguage(
      PublicacionResultado publicado, String creadorRol) {
    return new OfertaCatalogo(
        publicado.publicacionId(),
        TipoMaterialFiltro.valueOf(publicado.tipoResiduo()),
        publicado.pesoKg(),
        publicado.precioPorKilo(),
        publicado.latitud(),
        publicado.longitud(),
        publicado.estado(),
        creadorRol);
  }
}
