package com.barriocircular.backend.perfiles.aplicacion.comandos;

import java.util.UUID;

public record ActualizarDatosPerfilCommand(
    UUID perfilId,
    String correoElectronico,
    String telefono,
    Double latitud,
    Double longitud,
    String rol) {}
