package com.barriocircular.backend.acceso.dominio.modelo.agregados;

import com.barriocircular.backend.acceso.dominio.eventos.EventoDominio;
import com.barriocircular.backend.acceso.dominio.eventos.UsuarioRegistrado;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CuentaNoVerificadaException;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CuentaSuspendidaException;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.EstadoTransicionInvalidaException;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuentaAcceso {
    private final IdentificadorCuenta cuentaId;
    private final IdentificadorUsuarioClerk clerkId;
    private CorreoElectronico correoElectronico;
    private EstadoSesion estadoSesion;
    private final List<EventoDominio> eventosDominio = new ArrayList<>();

    private CuentaAcceso(IdentificadorUsuarioClerk clerkId, CorreoElectronico correoElectronico) {
        this.clerkId = clerkId;
        this.cuentaId = IdentificadorCuenta.nuevo();
        this.estadoSesion = EstadoSesion.PENDIENTE_VERIFICACION;
        this.correoElectronico = correoElectronico;
    }

    private CuentaAcceso(IdentificadorCuenta cuentaId, IdentificadorUsuarioClerk clerkId,
                         CorreoElectronico correoElectronico, EstadoSesion estadoSesion) {
        this.cuentaId = cuentaId;
        this.clerkId = clerkId;
        this.correoElectronico = correoElectronico;
        this.estadoSesion = estadoSesion;
    }

    public static CuentaAcceso registrarNueva(IdentificadorUsuarioClerk clerkId, CorreoElectronico correoElectronico, boolean correoVerificadoEnClerk) {
        CuentaAcceso cuenta = new CuentaAcceso(clerkId, correoElectronico);
        cuenta.estadoSesion = correoVerificadoEnClerk ? EstadoSesion.ACTIVA : EstadoSesion.PENDIENTE_VERIFICACION;
        cuenta.registrar(new UsuarioRegistrado(
                cuenta.cuentaId.uuid(),
                cuenta.clerkId.valor(),
                cuenta.correoElectronico.correoElectronico(),
                Instant.now()));
        return cuenta;
    }

    public static CuentaAcceso reconstruir(IdentificadorCuenta cuentaId, IdentificadorUsuarioClerk clerkId,
                                           CorreoElectronico correoElectronico, EstadoSesion estadoSesion) {
        return new CuentaAcceso(cuentaId, clerkId, correoElectronico, estadoSesion);
    }

    public void verificarAccesoPermitido() {
        if (this.estadoSesion == EstadoSesion.ELIMINADA) {
            throw new EstadoTransicionInvalidaException("Una cuenta en estado ELIMINADA no puede acceder a la plataforma.");
        }
        if (this.estadoSesion == EstadoSesion.SUSPENDIDA) {
            throw new CuentaSuspendidaException(this.cuentaId.uuid().toString());
        }
        if (this.estadoSesion == EstadoSesion.PENDIENTE_VERIFICACION) {
            throw new CuentaNoVerificadaException(this.cuentaId.uuid().toString());
        }
    }

    public void activarCuenta() {
        verificarNoEliminada();
        this.estadoSesion = EstadoSesion.ACTIVA;
    }

    public void suspenderPorAdministrador() {
        verificarNoEliminada();
        this.estadoSesion = EstadoSesion.SUSPENDIDA;
    }

    public void marcarComoEliminada() {
        this.estadoSesion = EstadoSesion.ELIMINADA;
    }

    private void verificarNoEliminada() {
        if (this.estadoSesion == EstadoSesion.ELIMINADA) {
            throw new EstadoTransicionInvalidaException("Una cuenta en estado ELIMINADA no puede modificar su estado operativo.");
        }
    }

    public List<EventoDominio> obtenerEventosDominio() {
        return Collections.unmodifiableList(eventosDominio);
    }

    public void limpiarEventosDominio() {
        eventosDominio.clear();
    }

    private void registrar(EventoDominio evento) {
        eventosDominio.add(evento);
    }

    public IdentificadorCuenta getCuentaId() {
        return cuentaId;
    }

    public IdentificadorUsuarioClerk getClerkId() {
        return clerkId;
    }

    public CorreoElectronico getCorreoElectronico() {
        return correoElectronico;
    }

    public EstadoSesion getEstadoSesion() {
        return estadoSesion;
    }
}