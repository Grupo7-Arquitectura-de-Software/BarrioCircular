package com.barriocircular.backend.acceso.dominio.modelo.objetosValor;

import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CorreoElectronicoExcepcion;

import java.util.regex.Pattern;

public record CorreoElectronico(String correoElectronico) {
    public static Pattern validarPatronCorreoElectronico = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

    public CorreoElectronico {
        if (correoElectronico == null || correoElectronico.isBlank()) {
            throw new CorreoElectronicoExcepcion(correoElectronico,
                    "El correo electronico no puede ser vacio");
        }
        correoElectronico = correoElectronico.trim().toLowerCase();
        if (!validarPatronCorreoElectronico.matcher(correoElectronico).matches()) {
            throw new CorreoElectronicoExcepcion(
                    correoElectronico, "El correo electronico no sigue el estandar RFC 5322");
        }
    }
}