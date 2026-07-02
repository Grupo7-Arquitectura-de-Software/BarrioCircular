package com.barriocircular.backend.publicacion.aplicacion.comandos;

import java.util.UUID;

public record ReservarPublicacionCommand(UUID publicacionId, UUID reservadorId) {}
