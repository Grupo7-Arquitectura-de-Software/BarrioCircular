package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class CorreoElectronicoExcepcion extends AccesoDominioExcepcion {
  public CorreoElectronicoExcepcion(String correoElectronico, String razon) {
    super(
        String.format(
            "El correo electrónico '%s' no es válido. Motivo: %s",
            correoElectronico != null ? correoElectronico : "NULO", razon));
  }
}
