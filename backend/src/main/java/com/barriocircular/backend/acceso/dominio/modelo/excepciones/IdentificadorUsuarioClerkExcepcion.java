package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class IdentificadorUsuarioClerkExcepcion extends AccesoDominioExcepcion {
  public IdentificadorUsuarioClerkExcepcion(String identificadorClerk, String razon) {
    super(
        String.format(
            "El UserID de Clerk '%s' no es válido. Motivo: %s",
            identificadorClerk != null ? identificadorClerk : "NULO", razon));
  }
}
