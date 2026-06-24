package com.barriocircular.backend.perfiles.aplicacion.casosdeuso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.barriocircular.backend.perfiles.aplicacion.dto.PerfilCapacidadResult;
import com.barriocircular.backend.perfiles.aplicacion.excepciones.PerfilNoEncontradoException;
import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.repositorios.PerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;

class ConsultarCapacidadesPerfilUseCaseTest {

    private final PerfilUsuarioRepositoryFake repository = new PerfilUsuarioRepositoryFake();
    private final ConsultarCapacidadesPerfilUseCase useCase = new ConsultarCapacidadesPerfilUseCase(repository);
    private final AtomicInteger secuenciaDocumento = new AtomicInteger(1000);

    @Test
    void ciudadanoActivoPuedePublicarMateriales() {
        PerfilUsuario perfil = guardarPerfil(RolUsuario.CIUDADANO);

        PerfilCapacidadResult result = useCase.ejecutar(perfil.getId());

        assertTrue(result.puedePublicarMateriales());
        assertFalse(result.puedeComprarMateriales());
        assertEquals("CIUDADANO", result.rolUsuario());
        assertInstanceOf(PerfilCapacidadResult.class, result);
    }

    @Test
    void recicladorActivoPuedePublicarMateriales() {
        PerfilUsuario perfil = guardarPerfil(RolUsuario.RECICLADOR);

        PerfilCapacidadResult result = useCase.ejecutar(perfil.getId());

        assertTrue(result.puedePublicarMateriales());
        assertTrue(result.puedeComprarMateriales());
        assertEquals("RECICLADOR", result.rolUsuario());
    }

    @Test
    void centroRecoleccionActivoNoPuedePublicarMateriales() {
        PerfilUsuario perfil = guardarPerfil(RolUsuario.CENTRO_RECOLECCION);

        PerfilCapacidadResult result = useCase.ejecutar(perfil.getId());

        assertFalse(result.puedePublicarMateriales());
        assertTrue(result.puedeComprarMateriales());
        assertEquals("CENTRO_RECOLECCION", result.rolUsuario());
    }

    @Test
    void perfilSuspendidoNoPuedePublicarNiComprarMateriales() {
        PerfilUsuario perfil = guardarPerfil(RolUsuario.RECICLADOR);
        perfil.suspender();
        repository.guardar(perfil);

        PerfilCapacidadResult result = useCase.ejecutar(perfil.getId());

        assertFalse(result.puedePublicarMateriales());
        assertFalse(result.puedeComprarMateriales());
        assertEquals("SUSPENDIDO", result.estadoPerfil());
    }

    @Test
    void perfilInexistenteDebeLanzarExcepcionClara() {
        UUID perfilId = UUID.randomUUID();

        assertThrows(PerfilNoEncontradoException.class, () -> useCase.ejecutar(perfilId));
    }

    private PerfilUsuario guardarPerfil(RolUsuario rol) {
        PerfilUsuario perfil = PerfilUsuarioFactory.crearPerfil(
                UUID.randomUUID(),
                documentoSegunRol(rol),
                rol == RolUsuario.CENTRO_RECOLECCION ? null : "Ana Perez",
                rol == RolUsuario.CENTRO_RECOLECCION ? "Centro Norte" : null,
                rol,
                new InformacionContacto("ana@correo.com", "0999999999"),
                new CoordenadaGPS(-0.1807, -78.4678));
        repository.guardar(perfil);
        return perfil;
    }

    private DocumentoIdentificacion documentoSegunRol(RolUsuario rol) {
        if (rol == RolUsuario.CENTRO_RECOLECCION) {
            return new DocumentoIdentificacion("1790012345001");
        }
        return new DocumentoIdentificacion("171234" + String.format("%04d", secuenciaDocumento.getAndIncrement()));
    }

    private static final class PerfilUsuarioRepositoryFake implements PerfilUsuarioRepository {

        private final Map<UUID, PerfilUsuario> perfiles = new HashMap<>();

        @Override
        public void guardar(PerfilUsuario perfil) {
            perfiles.put(perfil.getId(), perfil);
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
