package com.barriocircular.backend.acceso.dominio.repositorios;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.CorreoElectronico;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorUsuarioClerk;

import java.util.Optional;
import java.util.UUID;

public interface CuentaAccesoRepositorio {
    CuentaAcceso guardar(CuentaAcceso cuenta);

    Optional<CuentaAcceso> buscarPorId(UUID id);

    Optional<CuentaAcceso> buscarPorClerkId(IdentificadorUsuarioClerk id);

    boolean existePorCorreo(CorreoElectronico correo);
}
