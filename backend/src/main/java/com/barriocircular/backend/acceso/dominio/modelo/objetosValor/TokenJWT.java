package com.barriocircular.backend.acceso.dominio.modelo.objetosValor;

import com.barriocircular.backend.acceso.dominio.modelo.excepciones.TokenJWTExcepcion;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.TokenExpiradoException;

import java.time.Instant;

public record TokenJWT(String token, Instant fechaExpiracion) {
    public TokenJWT {
        if (token == null || token.isBlank()) {
            throw new TokenJWTExcepcion(token, "El token entregado por Clerk no puede ser nulo");
        }
        if (fechaExpiracion == null) {
            throw new TokenJWTExcepcion(token, "El token no contiene la fecha de expiración (claim 'exp')");
        }
        if (!fechaExpiracion.isAfter(Instant.now())) {
            throw new TokenExpiradoException(token);
        }
    }
}
