package com.barriocircular.backend.emparejamiento.infraestructura.persistencia.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEmparejamientoRepository
    extends JpaRepository<ResultadoEmparejamientoEntity, UUID> {

  List<ResultadoEmparejamientoEntity> findByCompradorIdOrderByFechaCalculoDesc(UUID compradorId);
}
