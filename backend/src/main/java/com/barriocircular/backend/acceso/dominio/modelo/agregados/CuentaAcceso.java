package com.barriocircular.backend.acceso.dominio.modelo.agregados;

import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CuentaNoVerificadaException;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CuentaSuspendidaException;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.EstadoTransicionInvalidaException;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.*;

public class CuentaAcceso {
    private final IdentificadorCuenta cuentaId;
    private final IdentificadorUsuarioClerk clerkId;
    private CorreoElectronico correoElectronico;
    private EstadoSesion estadoSesion;

    private CuentaAcceso(IdentificadorUsuarioClerk clerkId, CorreoElectronico correoElectronico) {
        this.clerkId = clerkId;
        this.cuentaId = IdentificadorCuenta.nuevo();
        this.estadoSesion = EstadoSesion.PENDIENTE_VERIFICACION;
        this.correoElectronico = correoElectronico;
    }

    public static CuentaAcceso registrarNueva(IdentificadorUsuarioClerk clerkId, CorreoElectronico correoElectronico, boolean correoVerificadoEnClerk) {
        CuentaAcceso cuenta = new CuentaAcceso(clerkId, correoElectronico);
        cuenta.estadoSesion = correoVerificadoEnClerk ? EstadoSesion.ACTIVA : EstadoSesion.PENDIENTE_VERIFICACION;
        return cuenta;
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