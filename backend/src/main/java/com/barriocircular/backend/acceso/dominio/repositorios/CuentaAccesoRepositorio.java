package com.barriocircular.backend.acceso.dominio.repositorios;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;

import java.util.Optional;

public interface CuentaAccesoRepositorio {
    void guardar(CuentaAcceso cuenta);

    Optional<CuentaAcceso> buscarPorClerkId(String id);

    boolean existePorCorreo(String correo);
}
