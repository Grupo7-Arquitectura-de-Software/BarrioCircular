package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.barriocircular.backend.perfiles.aplicacion.comandos.CrearPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.dominio.eventos.PerfilCreado;
import com.barriocircular.backend.perfiles.dominio.excepciones.DocumentoIdentificacionInvalidoException;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;

class CrearPerfilUseCaseTest {

    @Test
    void crearPerfilGuardaPublicaEventosYLosLimpia() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        CrearPerfilUseCase useCase = new CrearPerfilUseCase(repository, eventosPublicados);

        PerfilResultado resultado = useCase.ejecutar(new CrearPerfilCommand(
                UUID.randomUUID(),
                "1712345678",
                "Ana Perez",
                null,
                "CIUDADANO",
                "ana@correo.com",
                "0999999999",
                -0.1807,
                -78.4678));

        assertEquals(resultado.perfilId(), repository.perfilGuardado.getId());
        assertEquals("CIUDADANO", resultado.rol());
        assertEquals("ACTIVO", resultado.estadoPerfil());
        assertEquals("1712345678", resultado.documentoIdentificacion());
        assertEquals("ana@correo.com", resultado.correoElectronico());
        assertTrue(eventosPublicados.eventos.stream().anyMatch(PerfilCreado.class::isInstance));
        assertTrue(repository.perfilGuardado.obtenerEventosDominio().isEmpty());
    }

    @Test
    void crearPerfilConDocumentoInvalidoDebeLanzarExcepcionYNoGuardar() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        CrearPerfilUseCase useCase = new CrearPerfilUseCase(repository, eventosPublicados);

        assertThrows(DocumentoIdentificacionInvalidoException.class, () -> useCase.ejecutar(new CrearPerfilCommand(
                UUID.randomUUID(),
                "ABC123",
                "Ana Perez",
                null,
                "CIUDADANO",
                "ana@correo.com",
                "0999999999",
                -0.1807,
                -78.4678)));

        assertEquals(0, repository.guardados);
        assertTrue(eventosPublicados.eventos.isEmpty());
    }

    private static final class EventosPublicados implements ApplicationEventPublisher {

        private final List<Object> eventos = new ArrayList<>();

        @Override
        public void publishEvent(Object event) {
            eventos.add(event);
        }
    }

    private static final class PerfilUsuarioRepositoryFake implements PerfilUsuarioRepository {

        private PerfilUsuario perfilGuardado;
        private int guardados;

        @Override
        public void guardar(PerfilUsuario perfil) {
            this.perfilGuardado = perfil;
            guardados++;
        }

        @Override
        public Optional<PerfilUsuario> buscarPorId(UUID id) {
            return Optional.ofNullable(perfilGuardado)
                    .filter(perfil -> perfil.getId().equals(id));
        }

        @Override
        public Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId) {
            return Optional.ofNullable(perfilGuardado)
                    .filter(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId));
        }

        @Override
        public boolean existePorDocumentoIdentificacion(DocumentoIdentificacion documentoIdentificacion) {
            return perfilGuardado != null
                    && perfilGuardado.getDocumentoIdentificacion().equals(documentoIdentificacion);
        }

        @Override
        public boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId) {
            return perfilGuardado != null && perfilGuardado.getCuentaUsuarioId().equals(cuentaUsuarioId);
        }
    }
}
