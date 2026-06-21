package com.barriocircular.backend.perfiles.dominio.valueobjects;

import com.barriocircular.backend.perfiles.dominio.excepciones.DocumentoIdentificacionInvalidoException;

public record DocumentoIdentificacion(String valor) {

    private static final String FORMATO_DOCUMENTO = "^(\\d{10}|\\d{13})$";

    public DocumentoIdentificacion {
        if (valor == null || !valor.matches(FORMATO_DOCUMENTO)) {
            throw new DocumentoIdentificacionInvalidoException(
                    "El documento de identificacion debe ser una cedula de 10 digitos o un RUC de 13 digitos");
        }
    }

    public String getValor() {
        return valor;
    }
}
