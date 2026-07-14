package com.barriocircular.backend.verificacionidentidad.infraestructura.configuracion;

import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerificacionIdentidadProperties {

  private final String frontendBaseUrl;

  public VerificacionIdentidadProperties(
      @Value("${app.frontend-base-url}") String frontendBaseUrl) {
    this.frontendBaseUrl = frontendBaseUrl.replaceAll("/+$", "");
  }

  public String construirUrlPublica(TokenVerificacion token) {
    return frontendBaseUrl + "/verificar/" + token.valor();
  }
}
