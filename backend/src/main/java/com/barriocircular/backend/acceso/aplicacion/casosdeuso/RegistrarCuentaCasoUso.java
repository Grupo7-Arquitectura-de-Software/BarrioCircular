package com.barriocircular.backend.acceso.aplicacion.casosdeuso;

import com.barriocircular.backend.acceso.aplicacion.comandos.RegistrarCuentaCommand;
import com.barriocircular.backend.acceso.aplicacion.dto.RegistrarCuentaRespuesta;
import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CorreoDuplicadoException;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.DatosUsuarioVerificado;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.acceso.dominio.servicios.ValidadorIdentidad;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrarCuentaCasoUso {

    private final ValidadorIdentidad validadorIdentidad;
    private final CuentaAccesoRepositorio repositorio;
    private final ApplicationEventPublisher eventPublisher;

    public RegistrarCuentaCasoUso(ValidadorIdentidad validadorIdentidad,
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
            return new RegistrarCuentaRespuesta(cuenta.getCuentaId().uuid(), cuenta.getEstadoSesion(), false);
        }

        if (repositorio.existePorCorreo(datos.correoElectronico().correoElectronico())) {
            throw new CorreoDuplicadoException(datos.correoElectronico().correoElectronico());
        }

        CuentaAcceso nueva = CuentaAcceso.registrarNueva(
                datos.clerkId(), datos.correoElectronico(), datos.correoVerificado());

        repositorio.guardar(nueva);
        nueva.obtenerEventosDominio().forEach(eventPublisher::publishEvent);
        nueva.limpiarEventosDominio();

        return new RegistrarCuentaRespuesta(nueva.getCuentaId().uuid(), nueva.getEstadoSesion(), true);
    }
}