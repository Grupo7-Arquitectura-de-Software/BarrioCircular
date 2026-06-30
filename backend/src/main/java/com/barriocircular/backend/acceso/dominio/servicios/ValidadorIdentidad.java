package com.barriocircular.backend.acceso.dominio.servicios;

import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.DatosUsuarioVerificado;

public interface ValidadorIdentidad {
    DatosUsuarioVerificado validarUsuario(String tokenCrudo);
}
