package com.barriocircular.backend.verificacionidentidad.infraestructura.configuracion;

import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.UrlVerificacionBuilder;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import org.springframework.stereotype.Component;

@Component
public class UrlVerificacionBuilderAdapter implements UrlVerificacionBuilder {

  private final VerificacionIdentidadProperties properties;

  public UrlVerificacionBuilderAdapter(VerificacionIdentidadProperties properties) {
    this.properties = properties;
  }

  @Override
  public String construir(TokenVerificacion token) {
    return properties.construirUrlPublica(token);
  }
}
