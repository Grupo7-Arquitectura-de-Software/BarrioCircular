package com.barriocircular.backend.verificacionidentidad.dominio.repositorios;

import com.barriocircular.backend.verificacionidentidad.dominio.modelo.CredencialVerificacion;
import com.barriocircular.backend.verificacionidentidad.dominio.modelo.TokenVerificacion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CredencialVerificacionRepositorio {

  CredencialVerificacion guardar(CredencialVerificacion credencial);

  Optional<CredencialVerificacion> buscarPorToken(TokenVerificacion token);

  Optional<CredencialVerificacion> buscarActivaPorPerfil(UUID perfilId);

  List<CredencialVerificacion> listarPorPerfil(UUID perfilId);
}
