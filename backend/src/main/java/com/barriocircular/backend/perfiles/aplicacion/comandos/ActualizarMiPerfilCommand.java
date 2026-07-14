package com.barriocircular.backend.perfiles.aplicacion.comandos;

public record ActualizarMiPerfilCommand(
    String clerkIdAutenticado,
    String nombre,
    String apellido,
    String telefono,
    String direccion,
    Double latitud,
    Double longitud) {}
