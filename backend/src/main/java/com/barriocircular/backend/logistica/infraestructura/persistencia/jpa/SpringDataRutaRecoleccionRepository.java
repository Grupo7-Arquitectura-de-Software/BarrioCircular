package com.barriocircular.backend.logistica.infraestructura.persistencia.jpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRutaRecoleccionRepository
    extends JpaRepository<RutaRecoleccionEntity, UUID> {

  Optional<RutaRecoleccionEntity> findFirstByRecicladorIdAndFechaAndEstadoInOrderByHoraInicioAsc(
      UUID recicladorId, LocalDate fecha, Collection<String> estados);
}
