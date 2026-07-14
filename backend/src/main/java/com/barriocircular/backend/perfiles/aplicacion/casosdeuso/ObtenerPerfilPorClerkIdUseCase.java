package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.CuentaAccesoNoEncontradaException;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.aplicacion.mapeadores.PerfilResultadoMapper;
import com.barriocircular.backend.perfiles.aplicacion.puertos.CuentaAccesoConsultor;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPerfilPorClerkIdUseCase {

  private final CuentaAccesoConsultor cuentaAccesoConsultor;
  private final PerfilUsuarioRepository perfilUsuarioRepository;

  public ObtenerPerfilPorClerkIdUseCase(
      CuentaAccesoConsultor cuentaAccesoConsultor,
      PerfilUsuarioRepository perfilUsuarioRepository) {
    this.cuentaAccesoConsultor = cuentaAccesoConsultor;
    this.perfilUsuarioRepository = perfilUsuarioRepository;
  }

  @Transactional(readOnly = true)
  public PerfilResultado ejecutar(String clerkId) {
    UUID cuentaUsuarioId =
        cuentaAccesoConsultor
            .obtenerCuentaIdPorClerkId(clerkId)
            .orElseThrow(CuentaAccesoNoEncontradaException::new);

    PerfilUsuario perfilUsuario =
        perfilUsuarioRepository
            .buscarPorCuentaUsuarioId(cuentaUsuarioId)
            .orElseThrow(PerfilNoEncontradoException::new);

    return PerfilResultadoMapper.desde(perfilUsuario);
  }
}
