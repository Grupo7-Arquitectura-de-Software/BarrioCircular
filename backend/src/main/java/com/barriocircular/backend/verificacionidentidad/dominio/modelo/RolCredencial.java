package com.barriocircular.backend.verificacionidentidad.dominio.modelo;

import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.RolNoElegibleException;

public enum RolCredencial {
  RECICLADOR,
  CENTRO_RECOLECCION;

  public static RolCredencial desde(String rol) {
    try {
      return RolCredencial.valueOf(rol);
    } catch (IllegalArgumentException | NullPointerException excepcion) {
      throw new RolNoElegibleException(String.valueOf(rol));
    }
  }
}
