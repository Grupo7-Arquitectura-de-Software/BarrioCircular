package com.barriocircular.backend.emparejamiento.aplicacion.comandos;

import java.util.Set;

public record BuscarOfertasOptimasCommand(
    double latitud,
    double longitud,
    double radioMaximoKm,
    Set<String> tiposMaterial,
    String zonaDescriptiva) {}
