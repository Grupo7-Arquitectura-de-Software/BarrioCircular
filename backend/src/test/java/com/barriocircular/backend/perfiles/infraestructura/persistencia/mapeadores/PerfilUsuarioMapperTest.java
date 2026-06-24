package com.barriocircular.backend.perfiles.infraestructura.persistencia.mapeadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.EstadoPerfil;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.PerfilUsuarioEntity;

class PerfilUsuarioMapperTest {

    private final PerfilUsuarioMapper mapper = new PerfilUsuarioMapper();

    @Test
    void conviertePerfilUsuarioAEntidadJpa() {
        PerfilUsuario perfil = PerfilUsuarioFactory.crearPerfil(
                UUID.randomUUID(),
                new DocumentoIdentificacion("1712345678"),
                "Ana Perez",
                null,
                RolUsuario.CIUDADANO,
                new InformacionContacto("ana@correo.com", "0999999999"),
                new CoordenadaGPS(-0.1807, -78.4678));

        PerfilUsuarioEntity entity = mapper.toEntity(perfil);

        assertEquals(perfil.getId(), entity.getId());
        assertEquals(perfil.getCuentaUsuarioId(), entity.getCuentaUsuarioId());
        assertEquals("1712345678", entity.getDocumentoIdentificacion());
        assertEquals("CIUDADANO", entity.getRol());
        assertEquals("ACTIVO", entity.getEstadoPerfil());
        assertEquals("ana@correo.com", entity.getCorreoElectronico());
        assertEquals(-0.1807, entity.getLatitud());
        assertEquals(-78.4678, entity.getLongitud());
    }

    @Test
    void convierteEntidadJpaAPerfilUsuarioSinRegistrarEventoFalso() {
        UUID perfilId = UUID.randomUUID();
        UUID cuentaUsuarioId = UUID.randomUUID();
        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 6, 24, 10, 0);
        PerfilUsuarioEntity entity = new PerfilUsuarioEntity(
                perfilId,
                cuentaUsuarioId,
                "1790012345001",
                null,
                "Centro Norte",
                "CENTRO_RECOLECCION",
                "ACTIVO",
                "centro@correo.com",
                "022345678",
                -0.1807,
                -78.4678,
                fechaCreacion);

        PerfilUsuario perfil = mapper.toDomain(entity);

        assertEquals(perfilId, perfil.getId());
        assertEquals(cuentaUsuarioId, perfil.getCuentaUsuarioId());
        assertEquals("1790012345001", perfil.getDocumentoIdentificacion().getValor());
        assertEquals("centro@correo.com", perfil.getInformacionContacto().getCorreoElectronico());
        assertEquals("022345678", perfil.getInformacionContacto().getTelefono());
        assertEquals(-0.1807, perfil.getUbicacionHabitual().getLatitud());
        assertEquals(-78.4678, perfil.getUbicacionHabitual().getLongitud());
        assertEquals(RolUsuario.CENTRO_RECOLECCION, perfil.getRol());
        assertEquals(EstadoPerfil.ACTIVO, perfil.getEstadoPerfil());
        assertEquals(fechaCreacion, perfil.getFechaCreacion());
        assertTrue(perfil.obtenerEventosDominio().isEmpty());
    }
}
