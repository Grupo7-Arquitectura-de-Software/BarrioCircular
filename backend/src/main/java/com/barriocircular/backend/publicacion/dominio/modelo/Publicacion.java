package com.barriocircular.backend.publicacion.dominio.modelo;

import com.barriocircular.backend.publicacion.dominio.eventos.EventoDominio;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionCancelada;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionCreada;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionFinalizada;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionReservada;
import com.barriocircular.backend.publicacion.dominio.excepciones.EstadoInvalidoException;
import com.barriocircular.backend.publicacion.dominio.excepciones.PublicacionInvalidaException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Publicacion {

  private final PublicacionId id;
  private final CiudadanoId creador;
  private DetalleMaterial detalle;
  private PrecioPorKilo precioPorKilo;
  private UbicacionRecogida ubicacion;
  private EvidenciaVisual evidencia;
  private final Instant fechaCreacion;

  private EstadoPublicacion estado;
  private ReservadorId reservadoPor;
  private Double pesoRealVerificado;
  private String observacionesVerificacion;

  private final transient List<EventoDominio> eventos = new ArrayList<>();

  private Publicacion(
      PublicacionId id,
      CiudadanoId creador,
      DetalleMaterial detalle,
      PrecioPorKilo precioPorKilo,
      UbicacionRecogida ubicacion,
      EvidenciaVisual evidencia,
      Instant fechaCreacion,
      EstadoPublicacion estado,
      ReservadorId reservadoPor,
      Double pesoRealVerificado,
      String observacionesVerificacion) {
    this.id = id;
    this.creador = creador;
    this.detalle = detalle;
    this.precioPorKilo = precioPorKilo;
    this.ubicacion = ubicacion;
    this.evidencia = evidencia;
    this.fechaCreacion = fechaCreacion;
    this.estado = estado;
    this.reservadoPor = reservadoPor;
    this.pesoRealVerificado = pesoRealVerificado;
    this.observacionesVerificacion = normalizarObservaciones(observacionesVerificacion);
  }

  public static Publicacion crear(
      PublicacionId id,
      CiudadanoId creador,
      DetalleMaterial detalle,
      PrecioPorKilo precioPorKilo,
      UbicacionRecogida ubicacion,
      EvidenciaVisual evidencia) {
    exigir(id != null, "El id de la publicación es obligatorio.");
    exigir(creador != null, "El ciudadano creador es obligatorio.");
    exigir(detalle != null, "El detalle del material es obligatorio.");
    exigir(precioPorKilo != null, "El precio por kilo es obligatorio.");
    exigir(ubicacion != null, "La ubicación de recogida es obligatoria.");
    exigir(evidencia != null, "La evidencia visual es obligatoria.");

    Publicacion publicacion =
        new Publicacion(
            id,
            creador,
            detalle,
            precioPorKilo,
            ubicacion,
            evidencia,
            Instant.now(),
            EstadoPublicacion.DISPONIBLE,
            null,
            null,
            null);
    publicacion.registrar(new PublicacionCreada(id, creador, detalle.tipo(), Instant.now()));
    return publicacion;
  }

  public static Publicacion reconstituir(
      PublicacionId id,
      CiudadanoId creador,
      DetalleMaterial detalle,
      PrecioPorKilo precioPorKilo,
      UbicacionRecogida ubicacion,
      EvidenciaVisual evidencia,
      Instant fechaCreacion,
      EstadoPublicacion estado,
      ReservadorId reservadoPor,
      Double pesoRealVerificado,
      String observacionesVerificacion) {
    return new Publicacion(
        id,
        creador,
        detalle,
        precioPorKilo,
        ubicacion,
        evidencia,
        fechaCreacion,
        estado,
        reservadoPor,
        pesoRealVerificado,
        observacionesVerificacion);
  }

  public static Publicacion reconstituir(
      PublicacionId id,
      CiudadanoId creador,
      DetalleMaterial detalle,
      PrecioPorKilo precioPorKilo,
      UbicacionRecogida ubicacion,
      EvidenciaVisual evidencia,
      Instant fechaCreacion,
      EstadoPublicacion estado,
      ReservadorId reservadoPor) {
    return reconstituir(
        id,
        creador,
        detalle,
        precioPorKilo,
        ubicacion,
        evidencia,
        fechaCreacion,
        estado,
        reservadoPor,
        null,
        null);
  }

  public void reservarPublicacionPor(ReservadorId reservador) {
    Objects.requireNonNull(reservador, "El reservador es obligatorio.");
    if (reservador.valor().equals(creador.valor())) {
      throw new PublicacionInvalidaException("El creador no puede reservar su propia publicación.");
    }
    this.estado = estado.transicionarA(EstadoPublicacion.RESERVADA);
    this.reservadoPor = reservador;
    registrar(new PublicacionReservada(id, reservador, Instant.now()));
  }

  public void actualizarDatos(
      DetalleMaterial detalle,
      PrecioPorKilo precioPorKilo,
      UbicacionRecogida ubicacion,
      EvidenciaVisual evidencia) {
    exigir(detalle != null, "El detalle del material es obligatorio.");
    exigir(precioPorKilo != null, "El precio por kilo es obligatorio.");
    exigir(ubicacion != null, "La ubicación de recogida es obligatoria.");
    exigir(evidencia != null, "La evidencia visual es obligatoria.");
    if (estado != EstadoPublicacion.DISPONIBLE) {
      throw new EstadoInvalidoException(
          "Solo se puede editar una publicación mientras está DISPONIBLE.");
    }
    this.detalle = detalle;
    this.precioPorKilo = precioPorKilo;
    this.ubicacion = ubicacion;
    this.evidencia = evidencia;
  }

  public void finalizar() {
    this.estado = estado.transicionarA(EstadoPublicacion.FINALIZADA);
    registrar(new PublicacionFinalizada(id, reservadoPor, Instant.now()));
  }

  public void finalizarConVerificacion(double pesoRealVerificado, String observaciones) {
    validarPesoReal(pesoRealVerificado);
    String observacionesNormalizadas = normalizarObservaciones(observaciones);
    EstadoPublicacion estadoFinal = estado.transicionarA(EstadoPublicacion.FINALIZADA);
    this.pesoRealVerificado = pesoRealVerificado;
    this.observacionesVerificacion = observacionesNormalizadas;
    this.estado = estadoFinal;
    registrar(new PublicacionFinalizada(id, reservadoPor, Instant.now()));
  }

  public void cancelar() {
    this.estado = estado.transicionarA(EstadoPublicacion.CANCELADA);
    registrar(new PublicacionCancelada(id, Instant.now()));
  }

  private static void exigir(boolean condicion, String mensaje) {
    if (!condicion) {
      throw new PublicacionInvalidaException(mensaje);
    }
  }

  private static void validarPesoReal(double pesoRealVerificado) {
    exigir(
        Double.isFinite(pesoRealVerificado) && pesoRealVerificado > 0,
        "El peso real verificado debe ser mayor que 0 kg.");
  }

  private static String normalizarObservaciones(String observaciones) {
    if (observaciones == null) {
      return null;
    }
    String normalizadas = observaciones.trim();
    if (normalizadas.isEmpty()) {
      return null;
    }
    exigir(
        normalizadas.length() <= 1000,
        "Las observaciones de verificacion no pueden superar 1000 caracteres.");
    return normalizadas;
  }

  private void registrar(EventoDominio evento) {
    eventos.add(evento);
  }

  public List<EventoDominio> eventos() {
    return Collections.unmodifiableList(eventos);
  }

  public void limpiarEventos() {
    eventos.clear();
  }

  public PublicacionId id() {
    return id;
  }

  public CiudadanoId creador() {
    return creador;
  }

  public DetalleMaterial detalle() {
    return detalle;
  }

  public PrecioPorKilo precioPorKilo() {
    return precioPorKilo;
  }

  public UbicacionRecogida ubicacion() {
    return ubicacion;
  }

  public EvidenciaVisual evidencia() {
    return evidencia;
  }

  public Instant fechaCreacion() {
    return fechaCreacion;
  }

  public EstadoPublicacion estado() {
    return estado;
  }

  public ReservadorId reservadoPor() {
    return reservadoPor;
  }

  public Double pesoRealVerificado() {
    return pesoRealVerificado;
  }

  public String observacionesVerificacion() {
    return observacionesVerificacion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Publicacion otra)) {
      return false;
    }
    return id.equals(otra.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
