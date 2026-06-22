package com.barriocircular.backend.acceso.dominio.modelo.excepciones;

public class TokenExpiradoException extends AccesoDominioExcepcion {
    public TokenExpiradoException(String token) {
        super(String.format("El token de sesión '%s' ha expirado y ya no es válido.", enmascarar(token)));
    }

    private static String enmascarar(String token) {
        if (token == null || token.isBlank()) return "NULO";
        int visible = Math.min(8, token.length());
        return token.substring(0, visible) + "...";
    }
}