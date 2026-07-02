package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class CuentaSuspendidaException extends AccesoDominioExcepcion {
  public CuentaSuspendidaException(String cuentaId) {
    super(
        String.format(
            "La cuenta '%s' está suspendida y no puede acceder a la plataforma.", cuentaId));
  }
}
