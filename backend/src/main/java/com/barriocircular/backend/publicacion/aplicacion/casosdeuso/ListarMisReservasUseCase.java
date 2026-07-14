package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import com.barriocircular.backend.publicacion.aplicacion.dto.InfoContactoCreador;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarMisReservasUseCase {

  private final PublicacionRepositorio publicacionRepositorio;
  private final PerfilConsultor perfilConsultor;

  public ListarMisReservasUseCase(
      PublicacionRepositorio publicacionRepositorio,
      @Qualifier("perfilConsultorPublicacion") PerfilConsultor perfilConsultor) {
    this.publicacionRepositorio = publicacionRepositorio;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional(readOnly = true)
  public List<PublicacionResultado> ejecutar(String clerkIdAutenticado) {
    PerfilCapacidades perfil =
        perfilConsultor
            .obtenerCapacidadesPorClerkId(clerkIdAutenticado)
            .orElseThrow(PerfilNoEncontradoException::new);

    return publicacionRepositorio.listarPorReservador(ReservadorId.de(perfil.perfilId())).stream()
        .map(
            publicacion -> {
              InfoContactoCreador contacto =
                  perfilConsultor
                      .obtenerInfoContactoPorPerfilId(publicacion.creador().valor())
                      .orElse(null);
              String nombre = contacto != null ? contacto.nombre() : null;
              String telefono = contacto != null ? contacto.telefono() : null;
              return PublicacionResultado.desde(publicacion, nombre, telefono);
            })
        .toList();
  }
}
