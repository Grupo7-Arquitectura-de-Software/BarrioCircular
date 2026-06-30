package com.barriocircular.backend.perfiles.infraestructura.integracion.acceso;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;

@Component
public class CuentaAccesoConsultorAdapter implements CuentaAccesoConsultor {

    private final CuentaAccesoRepositorio repositorioCuentasAcceso;

    public CuentaAccesoConsultorAdapter(CuentaAccesoRepositorio repositorioCuentasAcceso) {
        this.repositorioCuentasAcceso = repositorioCuentasAcceso;
    }

    @Override
    public Optional<UUID> obtenerCuentaIdPorClerkId(String clerkId) {
        return repositorioCuentasAcceso.buscarPorClerkId(clerkId)
                .map(cuentaAcceso -> cuentaAcceso.getCuentaId().uuid());
    }
}
