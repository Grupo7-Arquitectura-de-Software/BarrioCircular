package com.barriocircular.backend.acceso.dominio.modelo.objetosValor;

public record DatosUsuarioVerificado(
    IdentificadorUsuarioClerk clerkId,
    CorreoElectronico correoElectronico,
    boolean correoVerificado) {}
