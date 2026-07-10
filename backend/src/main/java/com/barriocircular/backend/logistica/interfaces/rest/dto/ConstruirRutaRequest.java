package com.barriocircular.backend.logistica.interfaces.rest.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConstruirRutaRequest(LocalDate fechaRuta, LocalTime horaInicioRuta) {}
