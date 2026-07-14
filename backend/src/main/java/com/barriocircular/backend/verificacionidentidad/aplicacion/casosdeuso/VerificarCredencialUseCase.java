package com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso;

import com.barriocircular.backend.verificacionidentidad.aplicacion.dto.ResultadoVerificacionPublico;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilVerificable;
import com.barriocircular.backend.verificacionidentidad.dominio.excepciones.TokenVerificacionInvalidoException;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.repositorios.CredencialVerificacionRepositorio;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificarCredencialUseCase {

  private final CredencialVerificacionRepositorio credencialRepositorio;
  private final PerfilConsultor perfilConsultor;

  public VerificarCredencialUseCase(
      CredencialVerificacionRepositorio credencialRepositorio, PerfilConsultor perfilConsultor) {
    this.credencialRepositorio = credencialRepositorio;
    this.perfilConsultor = perfilConsultor;
  }

  @Transactional
  public ResultadoVerificacionPublico ejecutar(String tokenCrudo) {
    TokenVerificacion token;
    try {
      token = new TokenVerificacion(tokenCrudo);
    } catch (TokenVerificacionInvalidoException excepcion) {
      return ResultadoVerificacionPublico.invalido();
    }

    CredencialVerificacion credencial = credencialRepositorio.buscarPorToken(token).orElse(null);
    Instant ahora = Instant.now();
    if (credencial == null || !credencial.estaVigente(ahora)) {
      return ResultadoVerificacionPublico.invalido();
    }

    PerfilVerificable perfil =
        perfilConsultor.obtenerPorPerfilId(credencial.getPerfilId()).orElse(null);
    if (perfil == null || !perfil.perfilActivo() || !perfil.cuentaActiva()) {
      return ResultadoVerificacionPublico.invalido();
    }

    credencial.registrarVerificacion(ahora);
    credencialRepositorio.guardar(credencial);

    return ResultadoVerificacionPublico.valido(
        perfil.nombreMostrado(),
        perfil.rol(),
        credencial.getFechaEmision(),
        Duration.between(perfil.fechaRegistro(), ahora).toDays());
  }
}
