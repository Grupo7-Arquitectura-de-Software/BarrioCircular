package com.barriocircular.backend.perfiles.dominio.modelo;

import com.barriocircular.backend.perfiles.dominio.eventos.EventoDominio;
import com.barriocircular.backend.perfiles.dominio.eventos.PerfilActualizado;
import com.barriocircular.backend.perfiles.dominio.eventos.PerfilCreado;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilSuspendidoException;
import com.barriocircular.backend.perfiles.dominio.excepciones.RolInvalidoException;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PerfilUsuario {

  private final UUID id;
  private final UUID cuentaUsuarioId;
  private final DocumentoIdentificacion documentoIdentificacion;
  private String nombreCompleto;
  private final String nombreComercial;
  private RolUsuario rol;
  private EstadoPerfil estadoPerfil;
  private InformacionContacto informacionContacto;
  private CoordenadaGPS ubicacionHabitual;
  private String direccionHabitual;
  private final LocalDateTime fechaCreacion;
  private final List<EventoDominio> eventosDominio;

  private PerfilUsuario(
      UUID id,
      UUID cuentaUsuarioId,
      DocumentoIdentificacion documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      RolUsuario rol,
      EstadoPerfil estadoPerfil,
      InformacionContacto informacionContacto,
      CoordenadaGPS ubicacionHabitual,
      String direccionHabitual,
      LocalDateTime fechaCreacion) {
    this.id = Objects.requireNonNull(id, "El identificador del perfil es obligatorio");
    this.cuentaUsuarioId =
        Objects.requireNonNull(cuentaUsuarioId, "El identificador de la cuenta es obligatorio");
    this.documentoIdentificacion =
        Objects.requireNonNull(
            documentoIdentificacion, "El documento de identificacion es obligatorio");
    validarRol(rol);
    validarNombreSegunRol(rol, nombreCompleto, nombreComercial);
    this.nombreCompleto = normalizarNombre(nombreCompleto);
    this.nombreComercial = normalizarNombre(nombreComercial);
    this.rol = rol;
    this.estadoPerfil = Objects.requireNonNull(estadoPerfil, "El estado del perfil es obligatorio");
    this.informacionContacto =
        Objects.requireNonNull(informacionContacto, "La informacion de contacto es obligatoria");
    this.ubicacionHabitual =
        Objects.requireNonNull(ubicacionHabitual, "La ubicacion habitual es obligatoria");
    this.direccionHabitual = normalizarTextoOpcional(direccionHabitual);
    this.fechaCreacion =
        Objects.requireNonNull(fechaCreacion, "La fecha de creacion es obligatoria");
    this.eventosDominio = new ArrayList<>();
  }

  public static PerfilUsuario crear(
      UUID id,
      UUID cuentaUsuarioId,
      DocumentoIdentificacion documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      RolUsuario rol,
      EstadoPerfil estadoPerfil,
      InformacionContacto informacionContacto,
      CoordenadaGPS ubicacionHabitual,
      LocalDateTime fechaCreacion) {
    PerfilUsuario perfil =
        new PerfilUsuario(
            id,
            cuentaUsuarioId,
            documentoIdentificacion,
            nombreCompleto,
            nombreComercial,
            rol,
            estadoPerfil,
            informacionContacto,
            ubicacionHabitual,
            null,
            fechaCreacion);
    perfil.registrarEvento(
        new PerfilCreado(perfil.id, perfil.cuentaUsuarioId, perfil.rol, fechaCreacion));
    return perfil;
  }

  public static PerfilUsuario reconstituir(
      UUID id,
      UUID cuentaUsuarioId,
      DocumentoIdentificacion documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      RolUsuario rol,
      EstadoPerfil estadoPerfil,
      InformacionContacto informacionContacto,
      CoordenadaGPS ubicacionHabitual,
      LocalDateTime fechaCreacion) {
    return reconstituir(
        id,
        cuentaUsuarioId,
        documentoIdentificacion,
        nombreCompleto,
        nombreComercial,
        rol,
        estadoPerfil,
        informacionContacto,
        ubicacionHabitual,
        null,
        fechaCreacion);
  }

  public static PerfilUsuario reconstituir(
      UUID id,
      UUID cuentaUsuarioId,
      DocumentoIdentificacion documentoIdentificacion,
      String nombreCompleto,
      String nombreComercial,
      RolUsuario rol,
      EstadoPerfil estadoPerfil,
      InformacionContacto informacionContacto,
      CoordenadaGPS ubicacionHabitual,
      String direccionHabitual,
      LocalDateTime fechaCreacion) {
    return new PerfilUsuario(
        id,
        cuentaUsuarioId,
        documentoIdentificacion,
        nombreCompleto,
        nombreComercial,
        rol,
        estadoPerfil,
        informacionContacto,
        ubicacionHabitual,
        direccionHabitual,
        fechaCreacion);
  }

  public void actualizarInformacionContacto(InformacionContacto nuevaInformacionContacto) {
    verificarQueNoEsteSuspendido("actualizar su informacion de contacto");
    informacionContacto =
        Objects.requireNonNull(
            nuevaInformacionContacto, "La nueva informacion de contacto es obligatoria");
    registrarActualizacion("Informacion de contacto actualizada");
  }

  public void actualizarNombreCompleto(String nuevoNombreCompleto) {
    verificarQueNoEsteSuspendido("actualizar su nombre");
    validarNombreSegunRol(rol, nuevoNombreCompleto, nombreComercial);
    nombreCompleto = normalizarNombre(nuevoNombreCompleto);
    registrarActualizacion("Nombre completo actualizado");
  }

  public void actualizarUbicacionHabitual(CoordenadaGPS nuevaUbicacionHabitual) {
    verificarQueNoEsteSuspendido("actualizar su ubicacion habitual");
    ubicacionHabitual =
        Objects.requireNonNull(
            nuevaUbicacionHabitual, "La nueva ubicacion habitual es obligatoria");
    registrarActualizacion("Ubicacion habitual actualizada");
  }

  public void actualizarDireccionHabitual(String nuevaDireccionHabitual) {
    verificarQueNoEsteSuspendido("actualizar su direccion habitual");
    direccionHabitual = normalizarTextoOpcional(nuevaDireccionHabitual);
    registrarActualizacion("Direccion habitual actualizada");
  }

  public void cambiarRol(RolUsuario nuevoRol) {
    verificarQueNoEsteSuspendido("cambiar su rol");
    validarRol(nuevoRol);
    validarNombreSegunRol(nuevoRol, nombreCompleto, nombreComercial);
    rol = nuevoRol;
    registrarActualizacion("Rol actualizado a " + nuevoRol);
  }

  public void cambiarEstadoPerfil(EstadoPerfil nuevoEstadoPerfil) {
    estadoPerfil =
        Objects.requireNonNull(nuevoEstadoPerfil, "El nuevo estado del perfil es obligatorio");
  }

  public void suspender() {
    cambiarEstadoPerfil(EstadoPerfil.SUSPENDIDO);
  }

  public void activar() {
    cambiarEstadoPerfil(EstadoPerfil.ACTIVO);
  }

  public boolean estaActivo() {
    return estadoPerfil == EstadoPerfil.ACTIVO;
  }

  public boolean puedePublicarMateriales() {
    return estaActivo() && (rol == RolUsuario.CIUDADANO || rol == RolUsuario.RECICLADOR);
  }

  public boolean puedeComprarMateriales() {
    return estaActivo() && (rol == RolUsuario.RECICLADOR || rol == RolUsuario.CENTRO_RECOLECCION);
  }

  public List<EventoDominio> obtenerEventosDominio() {
    return Collections.unmodifiableList(eventosDominio);
  }

  public void limpiarEventosDominio() {
    eventosDominio.clear();
  }

  private void registrarActualizacion(String descripcionCambio) {
    registrarEvento(new PerfilActualizado(id, descripcionCambio, LocalDateTime.now()));
  }

  private void registrarEvento(EventoDominio evento) {
    eventosDominio.add(evento);
  }

  private void verificarQueNoEsteSuspendido(String accion) {
    if (estadoPerfil == EstadoPerfil.SUSPENDIDO) {
      throw new PerfilSuspendidoException("Un perfil suspendido no puede " + accion);
    }
  }

  private static void validarRol(RolUsuario rol) {
    if (rol == null) {
      throw new RolInvalidoException("Todo perfil debe tener un rol asignado");
    }
  }

  private static void validarNombreSegunRol(
      RolUsuario rol, String nombreCompleto, String nombreComercial) {
    if ((rol == RolUsuario.CIUDADANO || rol == RolUsuario.RECICLADOR)
        && !esNombreValido(nombreCompleto)) {
      throw new PerfilDomainException(
          "El nombre completo es obligatorio y debe tener al menos 2 caracteres");
    }
    if (rol == RolUsuario.CENTRO_RECOLECCION && !esNombreValido(nombreComercial)) {
      throw new PerfilDomainException(
          "El nombre comercial del centro de recoleccion es obligatorio y debe tener al menos 2 caracteres");
    }
  }

  private static boolean esNombreValido(String nombre) {
    return nombre != null && nombre.trim().length() >= 2;
  }

  private static String normalizarNombre(String nombre) {
    return nombre == null ? null : nombre.trim();
  }

  private static String normalizarTextoOpcional(String texto) {
    if (texto == null || texto.isBlank()) {
      return null;
    }
    return texto.trim();
  }

  public UUID getId() {
    return id;
  }

  public UUID getCuentaUsuarioId() {
    return cuentaUsuarioId;
  }

  public DocumentoIdentificacion getDocumentoIdentificacion() {
    return documentoIdentificacion;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }

  public String getNombreComercial() {
    return nombreComercial;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public EstadoPerfil getEstadoPerfil() {
    return estadoPerfil;
  }

  public InformacionContacto getInformacionContacto() {
    return informacionContacto;
  }

  public CoordenadaGPS getUbicacionHabitual() {
    return ubicacionHabitual;
  }

  public String getDireccionHabitual() {
    return direccionHabitual;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }
}
