package com.barriocircular.backend.logistica.dominio.objetosValor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public record HorarioParada(ZonedDateTime fechaHora) {

  public static final ZoneId ZONA_OPERATIVA = ZoneId.of("America/Guayaquil");

  public HorarioParada {
    Objects.requireNonNull(fechaHora, "El horario de parada es obligatorio.");
    fechaHora = fechaHora.withZoneSameInstant(ZONA_OPERATIVA);
  }

  public static HorarioParada de(LocalDate fecha, LocalTime hora) {
    Objects.requireNonNull(fecha, "La fecha del horario es obligatoria.");
    Objects.requireNonNull(hora, "La hora del horario es obligatoria.");
    return new HorarioParada(ZonedDateTime.of(fecha, hora, ZONA_OPERATIVA));
  }

  public HorarioParada mas(DuracionViaje duracion) {
    Objects.requireNonNull(duracion, "La duracion a sumar es obligatoria.");
    return new HorarioParada(fechaHora.plusMinutes(duracion.minutos()));
  }
}
