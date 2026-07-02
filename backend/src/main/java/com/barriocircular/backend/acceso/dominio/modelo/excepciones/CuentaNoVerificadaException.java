package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class CuentaNoVerificadaException extends AccesoDominioExcepcion {
  public CuentaNoVerificadaException(String cuentaId) {
    super(
        String.format(
            "La cuenta '%s' está pendiente de verificación y no puede acceder aún.", cuentaId));
  }
}
