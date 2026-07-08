package com.barriocircular.backend.verificacionidentidad.aplicacion.casosdeuso;

import com.barriocircular.backend.verificacionidentidad.aplicacion.dto.CredencialEmitidaResultado;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.CuentaNoActivaException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.PerfilNoActivoException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.GeneradorTokenPort;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.PerfilElegible;
import com.barriocircular.backend.verificacionidentidad.aplicacion.puertos.UrlVerificacionBuilder;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.RolCredencial;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.repositorios.CredencialVerificacionRepositorio;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmitirCredencialUseCase {

  private static final int VIGENCIA_EN_DIAS = 365;

  private final PerfilConsultor perfilConsultor;
  private final CredencialVerificacionRepositorio credencialRepositorio;
  private final GeneradorTokenPort generadorToken;
  private final UrlVerificacionBuilder urlVerificacionBuilder;

  public EmitirCredencialUseCase(
      PerfilConsultor perfilConsultor,
      CredencialVerificacionRepositorio credencialRepositorio,
      GeneradorTokenPort generadorToken,
      UrlVerificacionBuilder urlVerificacionBuilder) {
    this.perfilConsultor = perfilConsultor;
    this.credencialRepositorio = credencialRepositorio;
    this.generadorToken = generadorToken;
    this.urlVerificacionBuilder = urlVerificacionBuilder;
  }

  @Transactional
  public CredencialEmitidaResultado ejecutar(String clerkId) {
    PerfilElegible perfil =
        perfilConsultor.obtenerPorClerkId(clerkId).orElseThrow(PerfilNoEncontradoException::new);

    RolCredencial rolCredencial = RolCredencial.desde(perfil.rol());
    if (!perfil.perfilActivo()) {
      throw new PerfilNoActivoException();
    }
    if (!perfil.cuentaActiva()) {
      throw new CuentaNoActivaException();
    }

    CredencialVerificacion credencial =
        credencialRepositorio
            .buscarActivaPorPerfil(perfil.perfilId())
            .filter(credencialActiva -> credencialActiva.estaVigente(Instant.now()))
            .orElseGet(
                () ->
                    credencialRepositorio.guardar(
                        CredencialVerificacion.emitir(
                            perfil.perfilId(), rolCredencial, generarToken(), VIGENCIA_EN_DIAS)));

    return resultado(credencial);
  }

  private TokenVerificacion generarToken() {
    return generadorToken.generar();
  }

  private CredencialEmitidaResultado resultado(CredencialVerificacion credencial) {
    return new CredencialEmitidaResultado(
        credencial.getId(),
        urlVerificacionBuilder.construir(credencial.getToken()),
        credencial.getFechaEmision(),
        credencial.getFechaExpiracion());
  }
}
