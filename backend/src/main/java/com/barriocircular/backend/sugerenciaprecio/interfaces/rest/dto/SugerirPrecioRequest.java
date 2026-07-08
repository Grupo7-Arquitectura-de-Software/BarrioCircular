package com.barriocircular.backend.sugerenciaprecio.interfaces.rest.dto;

public record SugerirPrecioRequest(String tipoResiduo, Double pesoKg, String imagenBase64) {}
