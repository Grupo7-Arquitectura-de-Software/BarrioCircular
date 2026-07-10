package com.barriocircular.backend.logistica.dominio.modelo;

import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RutaRecoleccion {

  private final RutaRecoleccionId id;
  private final RecicladorId recicladorId;
  private final LocalDate fecha;
  private final LocalTime horaInicio;
  private final List<ParadaRecoleccion> paradas;

  private EstadoRutaRecoleccion estado;

  private RutaRecoleccion(
      RutaRecoleccionId id,
      RecicladorId recicladorId,
      LocalDate fecha,
      LocalTime horaInicio,
      EstadoRutaRecoleccion estado,
      List<ParadaRecoleccion> paradas) {
    this.id = Objects.requireNonNull(id, "El id de la ruta es obligatorio.");
    this.recicladorId =
        Objects.requireNonNull(recicladorId, "El reciclador de la ruta es obligatorio.");
    this.fecha = Objects.requireNonNull(fecha, "La fecha de la ruta es obligatoria.");
    this.horaInicio = Objects.requireNonNull(horaInicio, "La hora de inicio es obligatoria.");
    this.estado = Objects.requireNonNull(estado, "El estado de la ruta es obligatorio.");
    this.paradas = new ArrayList<>(validarParadas(paradas));
  }

  public static RutaRecoleccion planificar(
      RecicladorId recicladorId,
      LocalDate fecha,
      LocalTime horaInicio,
      List<ParadaRecoleccion> paradas) {
    return new RutaRecoleccion(
        RutaRecoleccionId.nuevo(),
        recicladorId,
        fecha,
        horaInicio,
        EstadoRutaRecoleccion.PLANIFICADA,
        paradas);
  }

  public void iniciar() {
    if (estado != EstadoRutaRecoleccion.PLANIFICADA) {
      throw new IllegalStateException("Solo una ruta planificada puede iniciar.");
    }
    estado = EstadoRutaRecoleccion.EN_CURSO;
  }

  public void completar() {
    if (estado != EstadoRutaRecoleccion.EN_CURSO) {
      throw new IllegalStateException("Solo una ruta en curso puede completarse.");
    }
    estado = EstadoRutaRecoleccion.COMPLETADA;
  }

  public void cancelar() {
    if (estado == EstadoRutaRecoleccion.COMPLETADA || estado == EstadoRutaRecoleccion.CANCELADA) {
      throw new IllegalStateException("La ruta no puede cancelarse desde su estado actual.");
    }
    estado = EstadoRutaRecoleccion.CANCELADA;
  }

  public void replanificar(List<ParadaRecoleccion> paradas) {
    Objects.requireNonNull(paradas, "Las paradas son obligatorias.");
    if (estado == EstadoRutaRecoleccion.COMPLETADA || estado == EstadoRutaRecoleccion.CANCELADA) {
      throw new IllegalStateException("No se puede replanificar una ruta que ya ha finalizado.");
    }
    List<ParadaRecoleccion> ordenadas = validarParadas(paradas);
    this.paradas.clear();
    this.paradas.addAll(ordenadas);
  }

  public void iniciarParada(ParadaRecoleccionId paradaId) {
    exigirRutaEnCurso();
    buscarParada(paradaId).iniciar();
  }

  public void completarParada(ParadaRecoleccionId paradaId, HorarioParada horarioReal) {
    exigirRutaEnCurso();
    buscarParada(paradaId).completar(horarioReal);
  }

  public void omitirParada(ParadaRecoleccionId paradaId) {
    exigirRutaEnCurso();
    buscarParada(paradaId).omitir();
  }

  private static List<ParadaRecoleccion> validarParadas(List<ParadaRecoleccion> paradas) {
    Objects.requireNonNull(paradas, "La ruta requiere una lista de paradas.");
    if (paradas.isEmpty()) {
      throw new IllegalArgumentException("La ruta debe tener al menos una parada.");
    }

    List<ParadaRecoleccion> ordenadas =
        paradas.stream()
            .map(parada -> Objects.requireNonNull(parada, "La parada no puede ser nula."))
            .sorted(Comparator.comparingInt(ParadaRecoleccion::orden))
            .toList();

    for (int indice = 0; indice < ordenadas.size(); indice++) {
      int ordenEsperado = indice + 1;
      if (ordenadas.get(indice).orden() != ordenEsperado) {
        throw new IllegalArgumentException("Las paradas deben tener ordenes consecutivos.");
      }
    }
    validarPublicacionesSinDuplicados(ordenadas);
    return ordenadas;
  }

  private static void validarPublicacionesSinDuplicados(List<ParadaRecoleccion> paradas) {
    long publicacionesUnicas =
        paradas.stream().map(ParadaRecoleccion::publicacionId).distinct().count();
    if (publicacionesUnicas != paradas.size()) {
      throw new IllegalArgumentException("La ruta no puede tener publicaciones duplicadas.");
    }
  }

  private void exigirRutaEnCurso() {
    if (estado != EstadoRutaRecoleccion.EN_CURSO) {
      throw new IllegalStateException("La ruta debe estar en curso para modificar sus paradas.");
    }
  }

  private ParadaRecoleccion buscarParada(ParadaRecoleccionId paradaId) {
    Objects.requireNonNull(paradaId, "El id de la parada es obligatorio.");
    return paradas.stream()
        .filter(parada -> parada.id().equals(paradaId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("La parada no pertenece a esta ruta."));
  }

  public RutaRecoleccionId id() {
    return id;
  }

  public RecicladorId recicladorId() {
    return recicladorId;
  }

  public LocalDate fecha() {
    return fecha;
  }

  public LocalTime horaInicio() {
    return horaInicio;
  }

  public EstadoRutaRecoleccion estado() {
    return estado;
  }

  public List<ParadaRecoleccion> paradas() {
    return Collections.unmodifiableList(paradas);
  }
}
