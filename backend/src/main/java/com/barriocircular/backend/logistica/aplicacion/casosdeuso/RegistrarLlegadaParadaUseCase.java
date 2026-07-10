package com.barriocircular.backend.logistica.aplicacion.casosdeuso;

import com.barriocircular.backend.logistica.aplicacion.dto.RutaRecoleccionResultado;
import com.barriocircular.backend.logistica.aplicacion.puertos.AlmacenRutaRecoleccionPort;
import com.barriocircular.backend.logistica.aplicacion.puertos.ReservasCatalogoPort;
import com.barriocircular.backend.logistica.dominio.modelo.ParadaRecoleccionId;
import com.barriocircular.backend.logistica.dominio.modelo.RutaRecoleccion;
import com.barriocircular.backend.logistica.dominio.objetosValor.HorarioParada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RegistrarLlegadaParadaUseCase {

  private final AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort;
  private final ReservasCatalogoPort reservasCatalogoPort;

  public RegistrarLlegadaParadaUseCase(
      AlmacenRutaRecoleccionPort almacenRutaRecoleccionPort,
      ReservasCatalogoPort reservasCatalogoPort) {
    this.almacenRutaRecoleccionPort =
        Objects.requireNonNull(almacenRutaRecoleccionPort, "El almacen de rutas es obligatorio.");
    this.reservasCatalogoPort =
        Objects.requireNonNull(reservasCatalogoPort, "El puerto de reservas es obligatorio.");
  }

  public RutaRecoleccionResultado ejecutar(
      UUID rutaId, UUID paradaId, LocalDate fechaLlegada, LocalTime horaLlegada) {
    validarEntrada(rutaId, paradaId, fechaLlegada, horaLlegada);

    RutaRecoleccion ruta =
        almacenRutaRecoleccionPort
            .buscarPorId(rutaId)
            .orElseThrow(() -> new IllegalStateException("No existe la ruta solicitada."));

    ruta.completarParada(
        ParadaRecoleccionId.de(paradaId), HorarioParada.de(fechaLlegada, horaLlegada));

    RutaRecoleccion guardada = almacenRutaRecoleccionPort.guardar(ruta);
    return RutaRecoleccionResultado.desde(
        guardada,
        reservasCatalogoPort.obtenerReservasActivasPorReciclador(guardada.recicladorId().valor()));
  }

  private void validarEntrada(
      UUID rutaId, UUID paradaId, LocalDate fechaLlegada, LocalTime horaLlegada) {
    Objects.requireNonNull(rutaId, "El id de la ruta es obligatorio.");
    Objects.requireNonNull(paradaId, "El id de la parada es obligatorio.");
    Objects.requireNonNull(fechaLlegada, "La fecha de llegada es obligatoria.");
    Objects.requireNonNull(horaLlegada, "La hora de llegada es obligatoria.");
  }
}
