package com.barriocircular.backend.perfiles.dominio.eventos;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;

public record PerfilCreado(
        UUID eventoId,
        UUID perfilId,
        UUID cuentaUsuarioId,
        RolUsuario rolUsuario,
        LocalDateTime ocurridoEn) implements EventoDominio {

    public PerfilCreado(UUID perfilId, UUID cuentaUsuarioId, RolUsuario rolUsuario, LocalDateTime ocurridoEn) {
        this(UUID.randomUUID(), perfilId, cuentaUsuarioId, rolUsuario, ocurridoEn);
    }

    public PerfilCreado {
        Objects.requireNonNull(eventoId, "El identificador del evento es obligatorio");
        Objects.requireNonNull(perfilId, "El identificador del perfil es obligatorio");
        Objects.requireNonNull(cuentaUsuarioId, "El identificador de la cuenta es obligatorio");
        Objects.requireNonNull(rolUsuario, "El rol del perfil es obligatorio");
        Objects.requireNonNull(ocurridoEn, "La fecha del evento es obligatoria");
    }
}
