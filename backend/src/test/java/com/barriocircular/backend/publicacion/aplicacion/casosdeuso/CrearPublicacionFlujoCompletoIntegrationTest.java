package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import static org.assertj.core.api.Assertions.assertThat;

import com.barriocircular.backend.publicacion.aplicacion.comandos.CrearPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionCreada;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import com.barriocircular.backend.publicacion.infraestructura.mensajeria.PerfilesContextoSimuladoListener;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
class CrearPublicacionFlujoCompletoIntegrationTest {

  @Autowired private CrearPublicacionUseCase crearPublicacionUseCase;

  @Autowired private PublicacionRepositorio publicacionRepositorio;

  @Autowired private ApplicationEvents eventos;

  @Autowired private PerfilesContextoSimuladoListener perfilesListener;

  @Test
  void creaPublicacionLaPersisteYDisparaEventoConElContratoHaciaPerfiles() {
    UUID creadorId = UUID.randomUUID();
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(
            creadorId,
            "PET",
            12.5,
            new BigDecimal("0.80"),
            -0.1807,
            -78.4678,
            "https://evidencia.barriocircular.com/foto.jpg");

    PublicacionResultado resultado = crearPublicacionUseCase.ejecutar(command);

    PublicacionId id = PublicacionId.de(resultado.publicacionId());
    assertThat(publicacionRepositorio.existePorId(id)).isTrue();

    Publicacion persistida = publicacionRepositorio.buscarPorId(id).orElseThrow();
    assertThat(persistida.creador().valor()).isEqualTo(creadorId);
    assertThat(persistida.detalle().tipo()).isEqualTo(TipoResiduo.PET);
    assertThat(persistida.estado()).isEqualTo(EstadoPublicacion.DISPONIBLE);

    assertThat(eventos.stream(PublicacionCreada.class).count()).isEqualTo(1);

    assertThat(perfilesListener.recibioEvento()).isTrue();
    assertThat(perfilesListener.creadorRecibido().valor()).isEqualTo(creadorId);
    assertThat(perfilesListener.tipoRecibido()).isEqualTo(TipoResiduo.PET);
  }

  @TestConfiguration
  static class ListenerDePruebaConfig {

    @Bean
    PerfilesContextoSimuladoListener perfilesContextoSimuladoListener() {
      return new PerfilesContextoSimuladoListener();
    }
  }
}
