package com.barriocircular.backend.acceso.infraestructura.persistencia.mapeadores;

import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.CorreoElectronico;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.EstadoSesion;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorCuenta;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorUsuarioClerk;
import com.barriocircular.backend.acceso.infraestructura.persistencia.jpa.CuentaAccesoEntity;
import org.springframework.stereotype.Component;

@Component
public class CuentaAccesoMapper {
    public CuentaAcceso alDominio(CuentaAccesoEntity entidadCuenta) {
        return CuentaAcceso.reconstruir(
                new IdentificadorCuenta(entidadCuenta.getIdentificadorCuenta()),
                new IdentificadorUsuarioClerk(entidadCuenta.getIdentificadorUsuarioClerk()),
                new CorreoElectronico(entidadCuenta.getCorreoElectronico()),
                EstadoSesion.valueOf(entidadCuenta.getEstadoSesion())
        );
    }

    public CuentaAccesoEntity aEntidad(CuentaAcceso cuentaAcceso) {
        CuentaAccesoEntity entidadAcesso = new CuentaAccesoEntity();
        entidadAcesso.setIdentificadorCuenta(cuentaAcceso.getCuentaId().uuid());
        entidadAcesso.setIdentificadorUsuarioClerk(cuentaAcceso.getClerkId().valor());
        entidadAcesso.setCorreoElectronico(cuentaAcceso.getCorreoElectronico().correoElectronico());
        entidadAcesso.setEstadoSesion(cuentaAcceso.getEstadoSesion().name());
        return entidadAcesso;
    }
}
