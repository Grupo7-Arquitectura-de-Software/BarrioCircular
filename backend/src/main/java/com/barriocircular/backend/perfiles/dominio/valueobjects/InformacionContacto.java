package com.barriocircular.backend.perfiles.dominio.valueobjects;

import com.barriocircular.backend.perfiles.dominio.excepciones.InformacionContactoInvalidaException;
import java.util.regex.Pattern;

public record InformacionContacto(String correoElectronico, String telefono) {

  private static final Pattern FORMATO_CORREO =
      Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$");
  private static final Pattern FORMATO_TELEFONO =
      Pattern.compile("^(?:09\\d{8}|0[2-7]\\d{7}|\\+593(?:9\\d{8}|[2-7]\\d{7}))$");

  public InformacionContacto {
    if (correoElectronico == null || !FORMATO_CORREO.matcher(correoElectronico).matches()) {
      throw new InformacionContactoInvalidaException(
          "El correo electronico del perfil no tiene un formato valido");
    }
    if (telefono == null || !FORMATO_TELEFONO.matcher(telefono).matches()) {
      throw new InformacionContactoInvalidaException(
          "El telefono del perfil debe tener un formato ecuatoriano local o internacional valido");
    }
  }

  public String getCorreoElectronico() {
    return correoElectronico;
  }

  public String getTelefono() {
    return telefono;
  }
}
