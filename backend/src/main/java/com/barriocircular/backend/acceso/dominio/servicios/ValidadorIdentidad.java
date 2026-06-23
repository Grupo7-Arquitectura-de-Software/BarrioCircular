package com.barriocircular.backend.acceso.dominio.servicios;

import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorUsuarioClerk;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.DatosUsuarioVerificado;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.TokenJWT;

public interface ValidadorIdentidad {
    DatosUsuarioVerificado validarUsuario(String tokenCrudo);

    DatosUsuarioVerificado obtenerUsuario(IdentificadorUsuarioClerk id);
}
