package com.barriocircular.backend.acceso.aplicacion.casosdeuso;

import com.barriocircular.backend.acceso.aplicacion.comandos.RegistrarCuentaCommand;
import com.barriocircular.backend.acceso.aplicacion.dto.RegistrarCuentaRespuesta;
import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CorreoDuplicadoException;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.DatosUsuarioVerificado;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.acceso.dominio.servicios.ValidadorIdentidad;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarCuentaCasoUso {

  private final ValidadorIdentidad validadorIdentidad;
  private final CuentaAccesoRepositorio repositorio;
  private final ApplicationEventPublisher eventPublisher;

  public RegistrarCuentaCasoUso(
      ValidadorIdentidad validadorIdentidad,
      CuentaAccesoRepositorio repositorio,
      ApplicationEventPublisher eventPublisher) {
    this.validadorIdentidad = validadorIdentidad;
    this.repositorio = repositorio;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public RegistrarCuentaRespuesta ejecutar(RegistrarCuentaCommand comando) {
    DatosUsuarioVerificado datos = validadorIdentidad.validarUsuario(comando.tokenClerk());

    Optional<CuentaAcceso> existente = repositorio.buscarPorClerkId(datos.clerkId().valor());
    if (existente.isPresent()) {
      CuentaAcceso cuenta = existente.get();
      return new RegistrarCuentaRespuesta(
          cuenta.getCuentaId().uuid(), cuenta.getEstadoSesion(), false);
    }

    Optional<CuentaAcceso> existentePorCorreo = repositorio.buscarPorCorreo(datos.correoElectronico().correoElectronico());
    if (existentePorCorreo.isPresent()) {
      CuentaAcceso cuenta = existentePorCorreo.get();
      // Si el correo ya existe, actualizamos el clerk ID (en caso de que haya cambiado)
      cuenta.actualizarClerkId(datos.clerkId());
      repositorio.guardar(cuenta);
      return new RegistrarCuentaRespuesta(
          cuenta.getCuentaId().uuid(), cuenta.getEstadoSesion(), false);
    }

    CuentaAcceso nueva =
        CuentaAcceso.registrarNueva(
            datos.clerkId(), datos.correoElectronico(), datos.correoVerificado());

    repositorio.guardar(nueva);
    nueva.obtenerEventosDominio().forEach(eventPublisher::publishEvent);
    nueva.limpiarEventosDominio();

    return new RegistrarCuentaRespuesta(nueva.getCuentaId().uuid(), nueva.getEstadoSesion(), true);
  }
}
