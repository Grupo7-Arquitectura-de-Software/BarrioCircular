package com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPerfilUsuarioRepository extends JpaRepository<PerfilUsuarioEntity, UUID> {

    Optional<PerfilUsuarioEntity> findByCuentaUsuarioId(UUID cuentaUsuarioId);

    boolean existsByCuentaUsuarioId(UUID cuentaUsuarioId);

    boolean existsByDocumentoIdentificacion(String documentoIdentificacion);
}
