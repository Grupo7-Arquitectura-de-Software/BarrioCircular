package com.barriocircular.backend.sugerenciaprecio.infraestructura.persistencia.jpa;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAnalisisMaterialRepository
    extends JpaRepository<AnalisisMaterialEntity, UUID> {}
