package com.barriocircular.backend.acceso.aplicacion.dto;

import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.EstadoSesion;
import java.util.UUID;

public record RegistrarCuentaRespuesta(UUID cuentaId, EstadoSesion estado, boolean esNueva) {}
