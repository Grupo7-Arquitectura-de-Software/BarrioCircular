package com.barriocircular.backend.verificacionidentidad.aplicacion.puertos;

import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;

public interface UrlVerificacionBuilder {

  String construir(TokenVerificacion token);
}
