package com.barriocircular.backend.emparejamiento.aplicacion.dto;

import java.util.UUID;

public record PerfilCapacidadesComprador(UUID perfilId, boolean puedeComprarMateriales) {}
