package com.barriocircular.backend.publicacion.infraestructura.integracion.perfiles;

import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.publicacion.aplicacion.dto.InfoContactoCreador;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import java.util.Optional;
import java.util.UUID;
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

  @Override
  public Optional<InfoContactoCreador> obtenerInfoContactoPorPerfilId(UUID perfilId) {
    return repositorioPerfiles
        .buscarPorId(perfilId)
        .map(
            perfil -> {
              String nombre =
                  perfil.getNombreComercial() != null
                      ? perfil.getNombreComercial()
                      : perfil.getNombreCompleto();
              String telefono = perfil.getInformacionContacto().getTelefono();
              return new InfoContactoCreador(nombre, telefono);
            });
  }
}
