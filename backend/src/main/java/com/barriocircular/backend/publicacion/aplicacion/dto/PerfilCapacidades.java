package com.barriocircular.backend.publicacion.aplicacion.dto;

import java.util.UUID;

public record PerfilCapacidades(
    UUID perfilId, boolean puedePublicarMateriales, boolean puedeComprarMateriales, String rol) {}
