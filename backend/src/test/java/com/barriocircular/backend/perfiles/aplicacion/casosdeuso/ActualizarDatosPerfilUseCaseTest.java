package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import com.barriocircular.backend.perfiles.aplicacion.comandos.ActualizarDatosPerfilCommand;
import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilResultado;
import com.barriocircular.backend.perfiles.dominio.eventos.PerfilActualizado;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilDomainException;
import com.barriocircular.backend.perfiles.dominio.excepciones.PerfilSuspendidoException;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;

class ActualizarDatosPerfilUseCaseTest {

    @Test
    void actualizaContactoGuardaPublicaEventosYLosLimpia() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        PerfilUsuario perfil = crearPerfil(RolUsuario.CIUDADANO);
        perfil.limpiarEventosDominio();
        repository.guardar(perfil);
        ActualizarDatosPerfilUseCase useCase = new ActualizarDatosPerfilUseCase(repository, eventosPublicados);

        PerfilResultado resultado = useCase.ejecutar(new ActualizarDatosPerfilCommand(
                perfil.getId(),
                "nuevo@correo.com",
                "0987654321",
                null,
                null,
                null));

        assertEquals("nuevo@correo.com", resultado.correoElectronico());
        assertEquals("0987654321", resultado.telefono());
        assertEquals(2, repository.guardados);
        assertTrue(eventosPublicados.eventos.stream().anyMatch(PerfilActualizado.class::isInstance));
        assertTrue(repository.perfiles.get(perfil.getId()).obtenerEventosDominio().isEmpty());
    }

    @Test
    void actualizaUbicacionHabitualCorrectamente() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        PerfilUsuario perfil = crearPerfil(RolUsuario.CIUDADANO);
        perfil.limpiarEventosDominio();
        repository.guardar(perfil);
        ActualizarDatosPerfilUseCase useCase = new ActualizarDatosPerfilUseCase(repository, eventosPublicados);

        PerfilResultado resultado = useCase.ejecutar(new ActualizarDatosPerfilCommand(
                perfil.getId(),
                null,
                null,
                -0.18,
                -78.48,
                null));

        assertEquals(-0.18, resultado.latitud());
        assertEquals(-78.48, resultado.longitud());
        assertTrue(eventosPublicados.eventos.stream().anyMatch(PerfilActualizado.class::isInstance));
    }

    @Test
    void cambiaRolSiElPerfilNoEstaSuspendido() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        PerfilUsuario perfil = crearPerfil(RolUsuario.CIUDADANO);
        perfil.limpiarEventosDominio();
        repository.guardar(perfil);
        ActualizarDatosPerfilUseCase useCase = new ActualizarDatosPerfilUseCase(repository, eventosPublicados);

        PerfilResultado resultado = useCase.ejecutar(new ActualizarDatosPerfilCommand(
                perfil.getId(),
                null,
                null,
                null,
                null,
                "RECICLADOR"));

        assertEquals("RECICLADOR", resultado.rol());
        assertTrue(eventosPublicados.eventos.stream().anyMatch(PerfilActualizado.class::isInstance));
        assertFalse(repository.perfiles.get(perfil.getId()).obtenerEventosDominio().contains(PerfilActualizado.class));
    }

    @Test
    void perfilInexistenteDebeLanzarExcepcionClara() {
        ActualizarDatosPerfilUseCase useCase = new ActualizarDatosPerfilUseCase(
                new PerfilUsuarioRepositoryFake(), new EventosPublicados());

        assertThrows(PerfilDomainException.class, () -> useCase.ejecutar(new ActualizarDatosPerfilCommand(
                UUID.randomUUID(),
                "nuevo@correo.com",
                null,
                null,
                null,
                null)));
    }

    @Test
    void perfilSuspendidoNoPermiteActualizarContactoNiRol() {
        PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
        EventosPublicados eventosPublicados = new EventosPublicados();
        PerfilUsuario perfil = crearPerfil(RolUsuario.CIUDADANO);
        perfil.suspender();
        perfil.limpiarEventosDominio();
        repository.guardar(perfil);
        ActualizarDatosPerfilUseCase useCase = new ActualizarDatosPerfilUseCase(repository, eventosPublicados);

        assertThrows(PerfilSuspendidoException.class, () -> useCase.ejecutar(new ActualizarDatosPerfilCommand(
                perfil.getId(),
                "nuevo@correo.com",
                null,
                null,
                null,
                null)));
        assertThrows(PerfilSuspendidoException.class, () -> useCase.ejecutar(new ActualizarDatosPerfilCommand(
                perfil.getId(),
                null,
                null,
                null,
                null,
                "RECICLADOR")));
    }

    private PerfilUsuario crearPerfil(RolUsuario rol) {
        return PerfilUsuarioFactory.crearPerfil(
                UUID.randomUUID(),
                new DocumentoIdentificacion("1712345678"),
                "Ana Perez",
                null,
                rol,
                new InformacionContacto("ana@correo.com", "0999999999"),
                new CoordenadaGPS(-0.1807, -78.4678));
    }

    private static final class EventosPublicados implements ApplicationEventPublisher {

        private final List<Object> eventos = new ArrayList<>();

        @Override
        public void publishEvent(Object event) {
            eventos.add(event);
        }
    }

    private static final class PerfilUsuarioRepositoryFake implements PerfilUsuarioRepository {

        private final Map<UUID, PerfilUsuario> perfiles = new HashMap<>();
        private int guardados;

        @Override
        public void guardar(PerfilUsuario perfil) {
            perfiles.put(perfil.getId(), perfil);
            guardados++;
        }

        @Override
        public Optional<PerfilUsuario> buscarPorId(UUID id) {
            return Optional.ofNullable(perfiles.get(id));
        }

        @Override
        public Optional<PerfilUsuario> buscarPorCuentaUsuarioId(UUID cuentaUsuarioId) {
            return perfiles.values().stream()
                    .filter(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId))
                    .findFirst();
        }

        @Override
        public boolean existePorDocumentoIdentificacion(DocumentoIdentificacion documentoIdentificacion) {
            return perfiles.values().stream()
                    .anyMatch(perfil -> perfil.getDocumentoIdentificacion().equals(documentoIdentificacion));
        }

        @Override
        public boolean existePorCuentaUsuarioId(UUID cuentaUsuarioId) {
            return perfiles.values().stream()
                    .anyMatch(perfil -> perfil.getCuentaUsuarioId().equals(cuentaUsuarioId));
        }
    }
}
