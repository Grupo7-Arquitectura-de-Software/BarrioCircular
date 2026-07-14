package com.barriocircular.backend.acceso.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.acceso.infraestructura.persistencia.jpa.CuentaAccesoEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCuentaAccesoRepository extends JpaRepository<CuentaAccesoEntity, UUID> {
  boolean existsByCorreoElectronico(String correoElectronico);

  Optional<CuentaAccesoEntity> findByCorreoElectronico(String correoElectronico);

  Optional<CuentaAccesoEntity> findByIdentificadorUsuarioClerk(String identificadorUsuarioClerk);

  Optional<CuentaAccesoEntity> findByIdentificadorCuenta(UUID identificadorCuenta);
}
