package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionPublicacionResultado;
import com.barriocircular.backend.logistica.aplicacion.dto.ConfirmacionRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ConfirmacionPublicacionPort;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.EstadoRutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccion;
import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccionId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmarRecoleccionUseCase {

  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final ConfirmacionPublicacionPort confirmacionPublicacionPort;

  public ConfirmarRecoleccionUseCase(
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort,
      ConfirmacionPublicacionPort confirmacionPublicacionPort) {
    this.almacenRutaRecoleccionPort =
        Objects.requireNonNull(almacenRutaRecoleccionPort, "El almacen de rutas es obligatorio.");
    this.confirmacionPublicacionPort =
        Objects.requireNonNull(
            confirmacionPublicacionPort,
            "El puerto de confirmacion de publicacion es obligatorio.");
  }

  @Transactional
  public ConfirmacionRecoleccionResultado ejecutar(
      UUID recolectorId,
      UUID rutaId,
      UUID paradaId,
      Double pesoRealVerificado,
      String observaciones) {
    validarEntrada(recolectorId, rutaId, paradaId);

    RutaRecoleccion ruta =
        almacenRutaRecoleccionPort
            .buscarPorId(rutaId)
            .orElseThrow(() -> new IllegalStateException("No existe la ruta solicitada."));

    if (!ruta.recicladorId().valor().equals(recolectorId)) {
      throw new IllegalStateException("La ruta no pertenece al recolector autenticado.");
    }
    if (ruta.estado() != EstadoRutaRecoleccion.EN_CURSO) {
      throw new IllegalStateException(
          "La ruta debe estar en curso para confirmar una recoleccion.");
    }

    ParadaRecoleccionId idParada = ParadaRecoleccionId.de(paradaId);
    ParadaRecoleccion parada = ruta.obtenerParada(idParada);
    if (parada.estado() != EstadoParadaRecoleccion.EN_PROGRESO) {
      throw new IllegalStateException("La parada debe estar en progreso para confirmarse.");
    }
    if (parada.horarioReal() == null) {
      throw new IllegalStateException("La parada no tiene hora real de llegada registrada.");
    }

    UUID publicacionId = parada.publicacionId().valor();
    validarPesoReal(pesoRealVerificado);
    ConfirmacionPublicacionResultado publicacionConfirmada =
        confirmacionPublicacionPort.confirmarRecoleccion(
            publicacionId, recolectorId, pesoRealVerificado, observaciones);

    ruta.completarParada(idParada, parada.horarioReal());
    RutaRecoleccion rutaGuardada = almacenRutaRecoleccionPort.guardar(ruta);
    ParadaRecoleccion paradaGuardada = rutaGuardada.obtenerParada(idParada);

    return new ConfirmacionRecoleccionResultado(
        rutaGuardada.id().valor(),
        rutaGuardada.estado().name(),
        paradaGuardada.id().valor(),
        paradaGuardada.estado().name(),
        publicacionConfirmada.publicacionId(),
        publicacionConfirmada.estadoPublicacion(),
        publicacionConfirmada.pesoRealVerificado(),
        rutaGuardada.estado() == EstadoRutaRecoleccion.COMPLETADA);
  }

  private void validarEntrada(UUID recolectorId, UUID rutaId, UUID paradaId) {
    Objects.requireNonNull(recolectorId, "El id del recolector es obligatorio.");
    Objects.requireNonNull(rutaId, "El id de la ruta es obligatorio.");
    Objects.requireNonNull(paradaId, "El id de la parada es obligatorio.");
  }

  private void validarPesoReal(Double pesoRealVerificado) {
    if (pesoRealVerificado == null
        || !Double.isFinite(pesoRealVerificado)
        || pesoRealVerificado <= 0) {
      throw new IllegalArgumentException("El peso real verificado debe ser mayor que 0 kg.");
    }
  }
}
