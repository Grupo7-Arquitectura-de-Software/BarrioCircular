package com.barriocircular.backend.logistica.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.RutaRecoleccionEntity;
import com.barriocircular.backend.logistica.infraestructura.persistencia.jpa.SpringDataRutaRecoleccionRepository;
import com.barriocircular.backend.logistica.infraestructura.persistencia.mapeadores.RutaRecoleccionMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RutaRecoleccionRepositorioAdapter implements AlmacenRutaRecoleccionPort {

  private static final List<String> ESTADOS_ACTIVOS =
      List.of(EstadoRutaRecoleccion.PLANIFICADA.name(), EstadoRutaRecoleccion.EN_CURSO.name());

  private final SpringDataRutaRecoleccionRepository springDataRepository;
  private final RutaRecoleccionMapper mapper;

  public RutaRecoleccionRepositorioAdapter(
      SpringDataRutaRecoleccionRepository springDataRepository, RutaRecoleccionMapper mapper) {
    this.springDataRepository =
        Objects.requireNonNull(
            springDataRepository, "El repositorio Spring Data de rutas es obligatorio.");
    this.mapper = Objects.requireNonNull(mapper, "El mapper de rutas es obligatorio.");
  }

  @Override
  public RutaRecoleccion guardar(RutaRecoleccion ruta) {
    Objects.requireNonNull(ruta, "La ruta a guardar es obligatoria.");
    Instant fechaCreacion =
        springDataRepository
            .findById(ruta.id().valor())
            .map(RutaRecoleccionEntity::getFechaCreacion)
            .orElseGet(Instant::now);
    RutaRecoleccionEntity guardada =
        springDataRepository.save(mapper.toEntity(ruta, fechaCreacion));
    return mapper.toDomain(guardada);
  }

  @Override
  public Optional<RutaRecoleccion> buscarPorId(UUID rutaId) {
    Objects.requireNonNull(rutaId, "El id de la ruta es obligatorio.");
    return springDataRepository.findById(rutaId).map(mapper::toDomain);
  }

  @Override
  public Optional<RutaRecoleccion> obtenerRutaActivaPorReciclador(UUID recicladorId) {
    Objects.requireNonNull(recicladorId, "El id del reciclador es obligatorio.");
    LocalDate fechaActual = LocalDate.now(HorarioParada.ZONA_OPERATIVA);
    return springDataRepository
        .findFirstByRecicladorIdAndFechaAndEstadoInOrderByHoraInicioAsc(
            recicladorId, fechaActual, ESTADOS_ACTIVOS)
        .map(mapper::toDomain);
  }
}
