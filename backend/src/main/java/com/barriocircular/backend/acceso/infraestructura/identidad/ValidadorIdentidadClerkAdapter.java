package com.barriocircular.backend.acceso.infraestructura.identidad;

import com.barriocircular.backend.acceso.dominio.modelo.excepciones.TokenJWTExcepcion;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.CorreoElectronico;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.DatosUsuarioVerificado;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.IdentificadorUsuarioClerk;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.TokenJWT;
import com.barriocircular.backend.acceso.dominio.servicios.ValidadorIdentidad;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorIdentidadClerkAdapter implements ValidadorIdentidad {

    private final JwtDecoder jwtDecoder;

    public ValidadorIdentidadClerkAdapter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public DatosUsuarioVerificado obtenerUsuario(IdentificadorUsuarioClerk id) {
        return null;
    }

    @Override
    public DatosUsuarioVerificado validarUsuario(String tokenCrudo) {
        try {
            Jwt jwt = jwtDecoder.decode(tokenCrudo);

            TokenJWT token = new TokenJWT(jwt.getTokenValue(), jwt.getExpiresAt());

            String clerkId = jwt.getSubject();
            String email = jwt.getClaim("email");
            Boolean emailVerificado = jwt.getClaim("email_verified");

            if (clerkId == null || email == null || emailVerificado == null) {
                throw new TokenJWTExcepcion(token.token(),
                        "El token no contiene los claims esperados (sub, email, email_verified)");
            }

            return new DatosUsuarioVerificado(
                    new IdentificadorUsuarioClerk(clerkId),
                    new CorreoElectronico(email),
                    emailVerificado);

        } catch (JwtException e) {
            throw new TokenJWTExcepcion(tokenCrudo, "Token inválido o firma no verificada, el error es el siguiente"+e);
        }
    }
}
