package com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPublicacionRepository extends JpaRepository<PublicacionEntity, UUID> {
}
