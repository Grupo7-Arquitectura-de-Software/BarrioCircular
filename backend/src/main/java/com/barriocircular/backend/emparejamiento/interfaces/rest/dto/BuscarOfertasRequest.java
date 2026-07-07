package com.barriocircular.backend.emparejamiento.interfaces.rest.dto;

import java.util.Set;

public record BuscarOfertasRequest(
        double latitud,
        double longitud,
        double radioMaximoKm,
        Set<String> tiposMaterial,
        String zonaDescriptiva) {
}
