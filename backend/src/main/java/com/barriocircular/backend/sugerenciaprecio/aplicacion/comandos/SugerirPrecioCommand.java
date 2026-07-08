package com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos;

public record SugerirPrecioCommand(String tipoResiduo, Double pesoKg, String imagenBase64) {}
