package com.barriocircular.backend.verificacionidentidad.infraestructura.tokens;

import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.GeneradorTokenPort;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class GeneradorTokenSeguroAdapter implements GeneradorTokenPort {

  private static final int BYTES_TOKEN = 32;
  private final SecureRandom secureRandom = new SecureRandom();

  @Override
  public TokenVerificacion generar() {
    byte[] bytes = new byte[BYTES_TOKEN];
    secureRandom.nextBytes(bytes);
    return new TokenVerificacion(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
  }
}
