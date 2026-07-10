package com.barriocircular.backend.publicacion.infraestructura.integracion.perfiles;

import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component("perfilConsultorPublicacion")
public class PerfilConsultorAdapter implements PerfilConsultor {

  private final CuentaAccesoRepositorio repositorioCuentasAcceso;
  private final PerfilUsuarioRepository repositorioPerfiles;

  public PerfilConsultorAdapter(
      CuentaAccesoRepositorio repositorioCuentasAcceso,
      PerfilUsuarioRepository repositorioPerfiles) {
    this.repositorioCuentasAcceso = repositorioCuentasAcceso;
    this.repositorioPerfiles = repositorioPerfiles;
  }

  @Override
  public Optional<PerfilCapacidades> obtenerCapacidadesPorClerkId(String clerkId) {
    return repositorioCuentasAcceso
        .buscarPorClerkId(clerkId)
        .map(cuentaAcceso -> cuentaAcceso.getCuentaId().uuid())
        .flatMap(repositorioPerfiles::buscarPorCuentaUsuarioId)
        .map(
            perfil ->
                new PerfilCapacidades(
                    perfil.getId(),
                    perfil.puedePublicarMateriales(),
                    perfil.puedeComprarMateriales(),
                    perfil.getRol().name()));
  }
}
