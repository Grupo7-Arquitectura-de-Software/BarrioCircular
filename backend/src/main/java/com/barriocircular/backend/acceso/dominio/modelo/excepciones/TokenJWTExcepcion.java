package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class TokenJWTExcepcion extends AccesoDominioExcepcion {
    public TokenJWTExcepcion(String token, String razon) {
        super(String.format("El JSON Web Token de Clerk no es válido. Motivo: %s. Token: %s",
                razon, enmascarar(token)));
    }

    private static String enmascarar(String token) {
        if (token == null || token.isBlank()) {
            return "NULO";
        }
        int longitudVisible = Math.min(8, token.length());
        return token.substring(0, longitudVisible) + "...";
    }
}
