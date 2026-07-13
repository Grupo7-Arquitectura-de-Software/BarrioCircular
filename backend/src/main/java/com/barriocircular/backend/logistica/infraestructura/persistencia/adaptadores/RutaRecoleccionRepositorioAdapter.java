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
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public RutaRecoleccion guardar(RutaRecoleccion ruta) {
    Objects.requireNonNull(ruta, "La ruta a guardar es obligatoria.");
    Instant fechaCreacion =
        springDataRepository
            .findById(ruta.id().valor())
            .map(RutaRecoleccionEntity::getFechaCreacion)
            .orElseGet(Instant::now);

    int maxAttempts = 3;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        RutaRecoleccionEntity entidadNueva = mapper.toEntity(ruta, fechaCreacion);
        RutaRecoleccionEntity guardada =
            springDataRepository
                .findById(ruta.id().valor())
                .map(
                    entidadExistente -> {
                      try {
                        java.lang.reflect.Field recicladorId =
                            RutaRecoleccionEntity.class.getDeclaredField("recicladorId");
                        java.lang.reflect.Field fecha =
                            RutaRecoleccionEntity.class.getDeclaredField("fecha");
                        java.lang.reflect.Field horaInicio =
                            RutaRecoleccionEntity.class.getDeclaredField("horaInicio");
                        java.lang.reflect.Field estado =
                            RutaRecoleccionEntity.class.getDeclaredField("estado");
                        java.lang.reflect.Field fechaCreacionField =
                            RutaRecoleccionEntity.class.getDeclaredField("fechaCreacion");
                        recicladorId.setAccessible(true);
                        fecha.setAccessible(true);
                        horaInicio.setAccessible(true);
                        estado.setAccessible(true);
                        fechaCreacionField.setAccessible(true);
                        recicladorId.set(entidadExistente, entidadNueva.getRecicladorId());
                        fecha.set(entidadExistente, entidadNueva.getFecha());
                        horaInicio.set(entidadExistente, entidadNueva.getHoraInicio());
                        estado.set(entidadExistente, entidadNueva.getEstado());
                        fechaCreacionField.set(entidadExistente, entidadNueva.getFechaCreacion());
                        entidadExistente.sincronizarParadas(entidadNueva.getParadas());
                      } catch (ReflectiveOperationException ex) {
                        throw new IllegalStateException(
                            "No fue posible actualizar la entidad de ruta existente.", ex);
                      }
                      return springDataRepository.save(entidadExistente);
                    })
                .orElseGet(() -> springDataRepository.save(entidadNueva));
        return mapper.toDomain(guardada);
      } catch (org.springframework.dao.OptimisticLockingFailureException
          | org.hibernate.StaleStateException ex) {
        if (attempt == maxAttempts) {
          throw ex;
        }
        try {
          Thread.sleep(100L * attempt);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new IllegalStateException("Retry interrupted", ie);
        }
        // reload fechaCreacion in case the existing entity was created earlier
        fechaCreacion =
            springDataRepository
                .findById(ruta.id().valor())
                .map(RutaRecoleccionEntity::getFechaCreacion)
                .orElseGet(Instant::now);
      }
    }
    throw new IllegalStateException("No fue posible guardar la ruta tras varios intentos.");
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
