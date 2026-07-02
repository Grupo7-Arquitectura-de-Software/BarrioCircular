package com.barriocircular.backend.publicacion.dominio.eventos;

import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import java.time.Instant;

public record PublicacionCreada(
    PublicacionId publicacionId, CiudadanoId creador, TipoResiduo tipo, Instant ocurridoEn)
    implements EventoDominio {}
