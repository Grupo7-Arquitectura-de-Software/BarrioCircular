package com.barriocircular.backend.sugerenciaprecio.infraestructura.configuracion;

import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra como beans de Spring las clases de dominio puras del contexto (sin anotaciones de
 * framework en {@code dominio.*}), para mantener ese paquete libre de dependencias externas. Los
 * precios base de mercado (USD/kg) se inyectan aquí desde la configuración (PRECIO_KG_*).
 */
@Configuration
public class SugerenciaPrecioBeansConfig {

  @Bean
  public CatalogoPreciosReferencia catalogoPreciosReferencia(
      @Value("${precios.referencia.pet}") BigDecimal precioPet,
      @Value("${precios.referencia.carton}") BigDecimal precioCarton,
      @Value("${precios.referencia.vidrio}") BigDecimal precioVidrio,
      @Value("${precios.referencia.chatarra}") BigDecimal precioChatarra) {
    return new CatalogoPreciosReferencia(precioPet, precioCarton, precioVidrio, precioChatarra);
  }
}
