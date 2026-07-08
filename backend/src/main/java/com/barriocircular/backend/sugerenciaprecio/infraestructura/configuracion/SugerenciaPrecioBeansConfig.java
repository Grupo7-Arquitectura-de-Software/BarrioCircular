package com.barriocircular.backend.sugerenciaprecio.infraestructura.configuracion;

import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra como beans de Spring las clases de dominio puras del contexto (sin anotaciones de
 * framework en {@code dominio.*}), para mantener ese paquete libre de dependencias externas.
 */
@Configuration
public class SugerenciaPrecioBeansConfig {

  @Bean
  public CatalogoPreciosReferencia catalogoPreciosReferencia() {
    return new CatalogoPreciosReferencia();
  }
}
