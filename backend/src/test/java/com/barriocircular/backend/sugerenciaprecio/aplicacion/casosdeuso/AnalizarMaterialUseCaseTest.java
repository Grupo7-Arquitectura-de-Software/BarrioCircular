package com.barriocircular.backend.sugerenciaprecio.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.barriocircular.backend.sugerenciaprecio.aplicacion.comandos.AnalizarMaterialCommand;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisIA;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.dto.AnalisisMaterialResultado;
import com.barriocircular.backend.sugerenciaprecio.aplicacion.puertos.AnalizadorMaterialIAPort;
import com.barriocircular.backend.sugerenciaprecio.dominio.excepciones.ImagenAnalisisInvalidaException;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.AnalisisMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.EstadoMaterial;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.ResultadoAnalisis;
import com.barriocircular.backend.sugerenciaprecio.dominio.modelo.TipoMaterialSugerido;
import com.barriocircular.backend.sugerenciaprecio.dominio.repositorios.AnalisisMaterialRepositorio;
import com.barriocircular.backend.sugerenciaprecio.dominio.servicios.CatalogoPreciosReferencia;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AnalizarMaterialUseCaseTest {

  private static final String IMAGEN_VALIDA = "data:image/jpeg;base64,ZmFrZS1pbWFnZQ==";

  private final CatalogoPreciosReferencia catalogo =
      new CatalogoPreciosReferencia(
          new BigDecimal("0.30"),
          new BigDecimal("0.10"),
          new BigDecimal("0.03"),
          new BigDecimal("0.25"));

  @Test
  void autocompletaTipoPesoYPrecioCuandoLaFotoEsValida() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, false, "PET", 2.5, "BUENO", "Botellas en buen estado."));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.VALIDO, resultado.resultado());
    assertEquals(TipoMaterialSugerido.PET, resultado.tipoMaterial());
    assertEquals(2.5, resultado.pesoEstimadoKg());
    assertEquals(EstadoMaterial.BUENO, resultado.estadoMaterial());
    assertEquals(new BigDecimal("0.27"), resultado.precioSugeridoPorKilo());
    assertEquals("Botellas en buen estado.", resultado.recomendacion());
    assertEquals(1, repositorio.cantidadGuardados);
  }

  @Test
  void elPrecioSiempreSaleDelCatalogoSegunElEstadoDelMaterial() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, false, "CHATARRA", 10.0, "REGULAR", null));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    // 0.25 x 0.8 = 0.20: precio base de mercado ajustado, nunca inventado por la IA.
    assertEquals(new BigDecimal("0.20"), resultado.precioSugeridoPorKilo());
  }

  @Test
  void reportaQueNoEsReciclajeSinSugerencias() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(false, true, false, null, null, null, "La foto muestra un gato."));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.NO_ES_RECICLAJE, resultado.resultado());
    assertNull(resultado.tipoMaterial());
    assertNull(resultado.precioSugeridoPorKilo());
    assertEquals("La foto muestra un gato.", resultado.recomendacion());
    assertEquals(1, repositorio.cantidadGuardados);
  }

  @Test
  void pideRepetirLaFotoConRecomendacionCuandoNoSeVeBien() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, false, false, "PET", null, null, "Acércate más al material."));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.FOTO_NO_CLARA, resultado.resultado());
    assertEquals("Acércate más al material.", resultado.recomendacion());
  }

  @Test
  void usaUnaRecomendacionPorDefectoSiLaFotoNoEsClaraYLaIaNoDioNinguna() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, false, false, null, null, null, null));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.FOTO_NO_CLARA, resultado.resultado());
    assertEquals(
        "Vuelve a tomar la foto con mejor luz y más cerca del material.",
        resultado.recomendacion());
  }

  @Test
  void pideMostrarUnSoloMaterialCuandoHayVariosTiposMezclados() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, true, "PET", 5.0, "BUENO", "Se ven botellas y cartón."));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.MULTIPLES_MATERIALES, resultado.resultado());
    assertNull(resultado.tipoMaterial());
    assertNull(resultado.precioSugeridoPorKilo());
  }

  @Test
  void rechazaUnMaterialReciclableFueraDelCatalogo() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, false, "OTRO", 3.0, "BUENO", "Parece ropa usada."));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.MATERIAL_NO_SOPORTADO, resultado.resultado());
    assertNull(resultado.precioSugeridoPorKilo());
  }

  @Test
  void descartaUnPesoAlucinadoFueraDeRangoSinInventarOtro() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, false, "PET", 5000.0, "BUENO", null));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.VALIDO, resultado.resultado());
    assertNull(resultado.pesoEstimadoKg());
    assertEquals(new BigDecimal("0.27"), resultado.precioSugeridoPorKilo());
  }

  @Test
  void asumeEstadoBuenoCuandoLaIaRespondeUnEstadoNoReconocido() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, true, false, "CARTON", 1.0, "COMO_NUEVO", null));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(EstadoMaterial.BUENO, resultado.estadoMaterial());
    assertEquals(new BigDecimal("0.09"), resultado.precioSugeridoPorKilo());
  }

  @Test
  void reportaIaNoDisponibleCuandoElPuertoDevuelveVacio() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA = AnalizadorMaterialIAPortFake.queDevuelve(null);
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.IA_NO_DISPONIBLE, resultado.resultado());
    assertEquals(1, repositorio.cantidadGuardados);
  }

  @Test
  void reportaIaNoDisponibleCuandoElPuertoLanzaUnaExcepcionInesperada() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queLanza(new RuntimeException("timeout"));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.IA_NO_DISPONIBLE, resultado.resultado());
  }

  @Test
  void reportaIaNoDisponibleCuandoLaRespuestaVieneSinLosVeredictosBooleanos() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA =
        AnalizadorMaterialIAPortFake.queDevuelve(
            new AnalisisIA(true, null, false, "PET", 2.0, "BUENO", null));
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    AnalisisMaterialResultado resultado =
        casoUso.ejecutar(new AnalizarMaterialCommand(IMAGEN_VALIDA), "user_123");

    assertEquals(ResultadoAnalisis.IA_NO_DISPONIBLE, resultado.resultado());
  }

  @Test
  void rechazaUnaImagenQueNoEsDataUriSinConsultarLaIA() {
    AnalisisMaterialRepositorioFake repositorio = new AnalisisMaterialRepositorioFake();
    AnalizadorMaterialIAPortFake puertoIA = AnalizadorMaterialIAPortFake.queDevuelve(null);
    AnalizarMaterialUseCase casoUso = new AnalizarMaterialUseCase(repositorio, puertoIA, catalogo);

    assertThrows(
        ImagenAnalisisInvalidaException.class,
        () -> casoUso.ejecutar(new AnalizarMaterialCommand(null), "user_123"));
    assertThrows(
        ImagenAnalisisInvalidaException.class,
        () -> casoUso.ejecutar(new AnalizarMaterialCommand("https://foto.jpg"), "user_123"));
    assertEquals(0, repositorio.cantidadGuardados);
  }

  private static final class AnalizadorMaterialIAPortFake implements AnalizadorMaterialIAPort {

    private final AnalisisIA valorADevolver;
    private final RuntimeException excepcionALanzar;

    private AnalizadorMaterialIAPortFake(
        AnalisisIA valorADevolver, RuntimeException excepcionALanzar) {
      this.valorADevolver = valorADevolver;
      this.excepcionALanzar = excepcionALanzar;
    }

    static AnalizadorMaterialIAPortFake queDevuelve(AnalisisIA valor) {
      return new AnalizadorMaterialIAPortFake(valor, null);
    }

    static AnalizadorMaterialIAPortFake queLanza(RuntimeException excepcion) {
      return new AnalizadorMaterialIAPortFake(null, excepcion);
    }

    @Override
    public Optional<AnalisisIA> analizar(String imagenBase64) {
      if (excepcionALanzar != null) {
        throw excepcionALanzar;
      }
      return Optional.ofNullable(valorADevolver);
    }
  }

  private static final class AnalisisMaterialRepositorioFake
      implements AnalisisMaterialRepositorio {

    private final List<AnalisisMaterial> guardados = new ArrayList<>();
    private int cantidadGuardados;

    @Override
    public AnalisisMaterial guardar(AnalisisMaterial analisisMaterial) {
      guardados.add(analisisMaterial);
      cantidadGuardados++;
      return analisisMaterial;
    }
  }
}
