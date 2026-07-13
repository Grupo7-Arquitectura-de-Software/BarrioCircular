package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import static org.assertj.core.api.Assertions.assertThat;

import com.barriocircular.backend.publicacion.aplicacion.comandos.CrearPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.InfoContactoCreador;
import com.barriocircular.backend.publicacion.aplicacion.dto.PerfilCapacidades;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.puertos.PerfilConsultor;
import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionCreada;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;
import com.barriocircular.backend.publicacion.infraestructura.mensajeria.PerfilesContextoSimuladoListener;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
class CrearPublicacionFlujoCompletoIntegrationTest {

  private static final String CLERK_ID_PUBLICADOR = "clerk_publicador_prueba";
  private static final UUID PERFIL_ID_PUBLICADOR =
      UUID.fromString("6f9d3a5e-1b2c-4d7e-8f90-123456789abc");

  @Autowired private CrearPublicacionUseCase crearPublicacionUseCase;

  @Autowired private PublicacionRepositorio publicacionRepositorio;

  @Autowired private ApplicationEvents eventos;

  @Autowired private PerfilesContextoSimuladoListener perfilesListener;

  @Test
  void creaPublicacionLaPersisteYDisparaEventoConElContratoHaciaPerfiles() {
    CrearPublicacionCommand command =
        new CrearPublicacionCommand(
            "PET",
            12.5,
            new BigDecimal("0.80"),
            -0.1807,
            -78.4678,
            "https://evidencia.barriocircular.com/foto.jpg");

    PublicacionResultado resultado = crearPublicacionUseCase.ejecutar(command, CLERK_ID_PUBLICADOR);

    PublicacionId id = PublicacionId.de(resultado.publicacionId());
    assertThat(publicacionRepositorio.existePorId(id)).isTrue();

    Publicacion persistida = publicacionRepositorio.buscarPorId(id).orElseThrow();
    assertThat(persistida.creador().valor()).isEqualTo(PERFIL_ID_PUBLICADOR);
    assertThat(persistida.detalle().tipo()).isEqualTo(TipoResiduo.PET);
    assertThat(persistida.estado()).isEqualTo(EstadoPublicacion.DISPONIBLE);

    assertThat(eventos.stream(PublicacionCreada.class).count()).isEqualTo(1);

    assertThat(perfilesListener.recibioEvento()).isTrue();
    assertThat(perfilesListener.creadorRecibido().valor()).isEqualTo(PERFIL_ID_PUBLICADOR);
    assertThat(perfilesListener.tipoRecibido()).isEqualTo(TipoResiduo.PET);
  }

  @TestConfiguration
  static class ListenerDePruebaConfig {

    @Bean
    PerfilesContextoSimuladoListener perfilesContextoSimuladoListener() {
      return new PerfilesContextoSimuladoListener();
    }

    @Bean
    @Primary
    PerfilConsultor perfilConsultorSimulado() {
      return new PerfilConsultor() {
        @Override
        public Optional<PerfilCapacidades> obtenerCapacidadesPorClerkId(String clerkId) {
          return CLERK_ID_PUBLICADOR.equals(clerkId)
              ? Optional.of(new PerfilCapacidades(PERFIL_ID_PUBLICADOR, true, false, "RECICLADOR"))
              : Optional.empty();
        }

        @Override
        public Optional<InfoContactoCreador> obtenerInfoContactoPorPerfilId(UUID perfilId) {
          return Optional.empty();
        }
      };
    }
  }
}
