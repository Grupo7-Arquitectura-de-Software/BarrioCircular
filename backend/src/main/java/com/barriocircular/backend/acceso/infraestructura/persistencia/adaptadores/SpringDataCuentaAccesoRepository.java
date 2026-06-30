package com.barriocircular.backend.acceso.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.acceso.infraestructura.persistencia.jpa.CuentaAccesoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface SpringDataCuentaAccesoRepository extends JpaRepository<CuentaAccesoEntity, UUID> {
    boolean existsByCorreoElectronico(String correoElectronico);

    Optional<CuentaAccesoEntity> findByIdentificadorUsuarioClerk(String identificadorUsuarioClerk);

    Optional<CuentaAccesoEntity> findByIdentificadorCuenta(UUID identificadorCuenta);
}