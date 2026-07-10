package com.barriocircular.backend.logistica.dominio.modelo;

import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import java.util.Objects;

public class ParadaRecoleccion {

  private final ParadaRecoleccionId id;
  private final PublicacionId publicacionId;
  private final CoordenadaGPS ubicacion;
  private final int orden;
  private final HorarioParada horarioEstimado;

  private HorarioParada horarioReal;
  private EstadoParadaRecoleccion estado;

  private ParadaRecoleccion(
      ParadaRecoleccionId id,
      PublicacionId publicacionId,
      CoordenadaGPS ubicacion,
      int orden,
      HorarioParada horarioEstimado,
      HorarioParada horarioReal,
      EstadoParadaRecoleccion estado) {
    this.id = Objects.requireNonNull(id, "El id de la parada es obligatorio.");
    this.publicacionId =
        Objects.requireNonNull(publicacionId, "La publicacion de la parada es obligatoria.");
    this.ubicacion = Objects.requireNonNull(ubicacion, "La ubicacion de la parada es obligatoria.");
    this.orden = validarOrden(orden);
    this.horarioEstimado =
        Objects.requireNonNull(horarioEstimado, "El horario estimado es obligatorio.");
    this.horarioReal = horarioReal;
    this.estado = Objects.requireNonNull(estado, "El estado de la parada es obligatorio.");
  }

  public static ParadaRecoleccion programar(
      PublicacionId publicacionId,
      CoordenadaGPS ubicacion,
      int orden,
      HorarioParada horarioEstimado) {
    return new ParadaRecoleccion(
        ParadaRecoleccionId.nuevo(),
        publicacionId,
        ubicacion,
        orden,
        horarioEstimado,
        null,
        EstadoParadaRecoleccion.PENDIENTE);
  }

  void iniciar() {
    if (estado != EstadoParadaRecoleccion.PENDIENTE) {
      throw new IllegalStateException("Solo una parada pendiente puede iniciar.");
    }
    estado = EstadoParadaRecoleccion.EN_PROGRESO;
  }

  void completar(HorarioParada horarioReal) {
    if (estado != EstadoParadaRecoleccion.PENDIENTE
        && estado != EstadoParadaRecoleccion.EN_PROGRESO) {
      throw new IllegalStateException("La parada no puede completarse desde su estado actual.");
    }
    this.horarioReal = Objects.requireNonNull(horarioReal, "El horario real es obligatorio.");
    estado = EstadoParadaRecoleccion.COMPLETADA;
  }

  void omitir() {
    if (estado != EstadoParadaRecoleccion.PENDIENTE
        && estado != EstadoParadaRecoleccion.EN_PROGRESO) {
      throw new IllegalStateException("La parada no puede omitirse desde su estado actual.");
    }
    estado = EstadoParadaRecoleccion.OMITIDA;
  }

  private static int validarOrden(int orden) {
    if (orden < 1) {
      throw new IllegalArgumentException("El orden de visita debe iniciar en 1.");
    }
    return orden;
  }

  public ParadaRecoleccionId id() {
    return id;
  }

  public PublicacionId publicacionId() {
    return publicacionId;
  }

  public CoordenadaGPS ubicacion() {
    return ubicacion;
  }

  public int orden() {
    return orden;
  }

  public HorarioParada horarioEstimado() {
    return horarioEstimado;
  }

  public HorarioParada horarioReal() {
    return horarioReal;
  }

  public EstadoParadaRecoleccion estado() {
    return estado;
  }
}
