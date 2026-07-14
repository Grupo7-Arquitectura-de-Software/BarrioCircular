package com.barriocircular.backend.acceso.infraestructura.persistencia.adaptadores;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.acceso.infraestructura.persistencia.mapeadores.CuentaAccesoMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CuentaAccesoRepositorioImpl implements CuentaAccesoRepositorio {
  private SpringDataCuentaAccesoRepository datacuentarepositorio;
  private CuentaAccesoMapper cuentaAccesoMapper;

  public CuentaAccesoRepositorioImpl(
      SpringDataCuentaAccesoRepository datacuentarepositorio,
      CuentaAccesoMapper cuentaAccesoMapper) {
    this.datacuentarepositorio = datacuentarepositorio;
    this.cuentaAccesoMapper = cuentaAccesoMapper;
  }

  @Override
  public void guardar(CuentaAcceso cuenta) {
    datacuentarepositorio.save(cuentaAccesoMapper.aEntidad(cuenta));
  }

  @Override
  public Optional<CuentaAcceso> buscarPorId(UUID id) {
    return datacuentarepositorio.findByIdentificadorCuenta(id).map(cuentaAccesoMapper::alDominio);
  }

  @Override
  public Optional<CuentaAcceso> buscarPorClerkId(String clerkId) {
    return datacuentarepositorio
        .findByIdentificadorUsuarioClerk(clerkId)
        .map(cuentaAccesoMapper::alDominio);
  }

  @Override
  public boolean existePorCorreo(String correo) {
    return datacuentarepositorio.existsByCorreoElectronico(correo);
  }

  @Override
  public Optional<CuentaAcceso> buscarPorCorreo(String correo) {
    return datacuentarepositorio.findByCorreoElectronico(correo).map(cuentaAccesoMapper::alDominio);
  }
}
