package com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.SugerirPrecioCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.SugerenciaPrecioResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.SugeridorPrecioIAPort;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.TipoMaterialSugeridoInvalidoException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.FuenteSugerencia;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecio;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.SugerenciaPrecioId;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.SugerenciaPrecioRepositorio;
import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SugerirPrecioUseCaseTest {

  @Test
  void usaLaSugerenciaDeIaCuandoElPuertoDevuelveUnValorValido() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA =
        SugeridorPrecioIAPortFake.queDevuelve(
            new SugerenciaIA(new BigDecimal("0.45"), "precio de mercado", true));
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    SugerenciaPrecioResultado resultado =
        casoUso.ejecutar(new SugerirPrecioCommand("PET", 10.0, null), "user_123");

    assertEquals(FuenteSugerencia.IA_GROQ, resultado.fuente());
    assertEquals(new BigDecimal("0.45"), resultado.precioSugeridoPorKilo());
    assertEquals("precio de mercado", resultado.justificacion());
    assertEquals(1, repositorio.cantidadGuardados);
  }

  @Test
  void usaElCatalogoDeRespaldoCuandoElPuertoDevuelveOptionalVacio() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA = SugeridorPrecioIAPortFake.queDevuelve(null);
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    SugerenciaPrecioResultado resultado =
        casoUso.ejecutar(new SugerirPrecioCommand("PET", 10.0, null), "user_123");

    assertEquals(FuenteSugerencia.CATALOGO_RESPALDO, resultado.fuente());
    assertEquals(BigDecimal.valueOf(0.30), resultado.precioSugeridoPorKilo());
    assertEquals(
        "Precio de referencia de catálogo: la IA no pudo generar una sugerencia para estos datos.",
        resultado.justificacion());
  }

  @Test
  void usaElCatalogoDeRespaldoCuandoElPrecioDeIaEstaFueraDeRango() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA =
        SugeridorPrecioIAPortFake.queDevuelve(
            new SugerenciaIA(new BigDecimal("999.99"), "alucinacion", true));
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    SugerenciaPrecioResultado resultado =
        casoUso.ejecutar(new SugerirPrecioCommand("CARTON", null, null), "user_123");

    assertEquals(FuenteSugerencia.CATALOGO_RESPALDO, resultado.fuente());
    assertEquals(BigDecimal.valueOf(0.10), resultado.precioSugeridoPorKilo());
  }

  @Test
  void usaElCatalogoDeRespaldoConAvisoCuandoLaImagenNoCoincideConElMaterial() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA =
        SugeridorPrecioIAPortFake.queDevuelve(
            new SugerenciaIA(new BigDecimal("0.45"), "no parece cartón", false));
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    SugerenciaPrecioResultado resultado =
        casoUso.ejecutar(
            new SugerirPrecioCommand("CARTON", 5.0, "data:image/jpeg;base64,x"), "user_123");

    assertEquals(FuenteSugerencia.CATALOGO_RESPALDO, resultado.fuente());
    assertEquals(BigDecimal.valueOf(0.10), resultado.precioSugeridoPorKilo());
    assertTrue(resultado.justificacion().contains("no parece corresponder al material declarado"));
  }

  @Test
  void usaElCatalogoDeRespaldoCuandoElPuertoLanzaUnaExcepcionInesperada() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA =
        SugeridorPrecioIAPortFake.queLanza(new RuntimeException("timeout"));
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    SugerenciaPrecioResultado resultado =
        casoUso.ejecutar(new SugerirPrecioCommand("VIDRIO", 3.0, null), "user_123");

    assertEquals(FuenteSugerencia.CATALOGO_RESPALDO, resultado.fuente());
    assertEquals(new BigDecimal("0.03"), resultado.precioSugeridoPorKilo());
  }

  @Test
  void rechazaUnTipoDeMaterialFueraDelCatalogo() {
    SugerenciaPrecioRepositorioFake repositorio = new SugerenciaPrecioRepositorioFake();
    SugeridorPrecioIAPortFake puertoIA = SugeridorPrecioIAPortFake.queDevuelve(null);
    SugerirPrecioUseCase casoUso =
        new SugerirPrecioUseCase(repositorio, puertoIA, new CatalogoPreciosReferencia());

    assertThrows(
        TipoMaterialSugeridoInvalidoException.class,
        () -> casoUso.ejecutar(new SugerirPrecioCommand("MADERA", null, null), "user_123"));
    assertEquals(0, repositorio.cantidadGuardados);
  }

  private static final class SugeridorPrecioIAPortFake implements SugeridorPrecioIAPort {

    private final SugerenciaIA valorADevolver;
    private final RuntimeException excepcionALanzar;

    private SugeridorPrecioIAPortFake(
        SugerenciaIA valorADevolver, RuntimeException excepcionALanzar) {
      this.valorADevolver = valorADevolver;
      this.excepcionALanzar = excepcionALanzar;
    }

    static SugeridorPrecioIAPortFake queDevuelve(SugerenciaIA valor) {
      return new SugeridorPrecioIAPortFake(valor, null);
    }

    static SugeridorPrecioIAPortFake queLanza(RuntimeException excepcion) {
      return new SugeridorPrecioIAPortFake(null, excepcion);
    }

    @Override
    public Optional<SugerenciaIA> sugerirPrecio(
        TipoMaterialSugerido tipoMaterial, Double pesoKg, String imagenBase64) {
      if (excepcionALanzar != null) {
        throw excepcionALanzar;
      }
      return Optional.ofNullable(valorADevolver);
    }
  }

  private static final class SugerenciaPrecioRepositorioFake
      implements SugerenciaPrecioRepositorio {

    private final List<SugerenciaPrecio> guardadas = new ArrayList<>();
    private int cantidadGuardados;

    @Override
    public SugerenciaPrecio guardar(SugerenciaPrecio sugerenciaPrecio) {
      guardadas.add(sugerenciaPrecio);
      cantidadGuardados++;
      return sugerenciaPrecio;
    }

    @Override
    public Optional<SugerenciaPrecio> buscarPorId(SugerenciaPrecioId id) {
      return guardadas.stream().filter(s -> s.id().equals(id)).findFirst();
    }

    @Override
    public List<SugerenciaPrecio> listarPorTipoMaterial(TipoMaterialSugerido tipoMaterial) {
      return guardadas.stream().filter(s -> s.tipoMaterial() == tipoMaterial).toList();
    }
  }
}
