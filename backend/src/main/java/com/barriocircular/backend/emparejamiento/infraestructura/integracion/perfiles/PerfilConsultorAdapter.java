package com.barriocircular.backend.emparejamiento.infraestructura.integracion.perfiles;

import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.emparejamiento.aplicacion.dto.PerfilCapacidadesComprador;
import com.barriocircular.backend.emparejamiento.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
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
    public Optional<PerfilCapacidadesComprador> obtenerCapacidadesPorClerkId(String clerkId) {
        return repositorioCuentasAcceso
                .buscarPorClerkId(clerkId)
                .map(cuentaAcceso -> cuentaAcceso.getCuentaId().uuid())
                .flatMap(repositorioPerfiles::buscarPorCuentaUsuarioId)
                .map(
                        perfil ->
                                new PerfilCapacidadesComprador(perfil.getId(), perfil.puedeComprarMateriales()));
    }
}