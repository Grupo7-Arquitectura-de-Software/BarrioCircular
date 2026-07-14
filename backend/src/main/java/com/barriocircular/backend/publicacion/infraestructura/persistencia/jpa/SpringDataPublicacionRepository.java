package com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPublicacionRepository extends JpaRepository<PublicacionEntity, UUID> {

  List<PublicacionEntity> findByEstadoOrderByFechaCreacionDesc(String estado);

  List<PublicacionEntity> findByCreadorIdOrderByFechaCreacionDesc(UUID creadorId);

  List<PublicacionEntity> findByReservadoPorOrderByFechaCreacionDesc(UUID reservadoPor);
}
