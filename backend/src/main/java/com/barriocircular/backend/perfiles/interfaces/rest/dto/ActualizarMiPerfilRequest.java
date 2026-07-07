package com.barriocircular.backend.perfiles.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.LinkedHashSet;
import java.util.Set;

public class ActualizarMiPerfilRequest {

  private static final Set<String> CAMPOS_RESTRINGIDOS =
      Set.of("perfilId", "id", "cuentaUsuarioId", "estado", "estadoPerfil", "rol");

  private String nombre;
  private String apellido;
  private String telefono;
  private String direccion;
  private Double latitud;
  private Double longitud;
  private final Set<String> camposNoPermitidos = new LinkedHashSet<>();

  public String nombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String apellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String telefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  public String direccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public Double latitud() {
    return latitud;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public Double longitud() {
    return longitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  @JsonAnySetter
  public void registrarCampoNoPermitido(String nombreCampo, Object valor) {
    camposNoPermitidos.add(nombreCampo);
  }

  public void validarCamposPermitidos() {
    Set<String> camposRestringidosRecibidos = new LinkedHashSet<>(camposNoPermitidos);
    camposRestringidosRecibidos.retainAll(CAMPOS_RESTRINGIDOS);
    if (!camposRestringidosRecibidos.isEmpty()) {
      throw new IllegalArgumentException(
          "No se permite actualizar campos restringidos: " + camposRestringidosRecibidos);
    }
    if (!camposNoPermitidos.isEmpty()) {
      throw new IllegalArgumentException(
          "La solicitud contiene campos no permitidos: " + camposNoPermitidos);
    }
  }
}
