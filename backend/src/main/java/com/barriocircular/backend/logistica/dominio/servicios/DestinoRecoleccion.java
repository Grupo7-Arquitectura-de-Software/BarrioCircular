package com.barriocircular.backend.logistica.dominio.servicios;

import com.barriocircular.backend.logistica.dominio.modelo.PublicacionId;
import com.barriocircular.backend.logistica.dominio.objetosValor.CoordenadaGPS;
import java.util.Objects;

public record DestinoRecoleccion(PublicacionId publicacionId, CoordenadaGPS ubicacion) {

  public DestinoRecoleccion {
    Objects.requireNonNull(publicacionId, "La publicacion destino es obligatoria.");
    Objects.requireNonNull(ubicacion, "La ubicacion destino es obligatoria.");
  }
}
