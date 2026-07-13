package com.barriocircular.backend.logistica.aplicacion.puertos;

import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionPublicacionResultado;
import java.util.UUID;

public interface ConfirmacionPublicacionPort {

  ConfirmacionPublicacionResultado confirmarRecoleccion(
      UUID publicacionId, UUID recolectorId, double pesoRealVerificado, String observaciones);
}
