package com.barriocircular.backend.perfiles.infraestructura.persistencia.adaptadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilUsuarioRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@ActiveProfiles("supabase")
@EnabledIfEnvironmentVariable(named = "BARRIO_CIRCULAR_RUN_DB_IT", matches = "true")
class PerfilUsuarioRepositoryAdapterIT {

  @Autowired private PerfilUsuarioRepositoryAdapter adapter;

  @Autowired private SpringDataPerfilUsuarioRepository springDataRepository;

  @Test
  void guardaYRecuperaPerfilUsuarioConValueObjects() {
    UUID cuentaUsuarioId = UUID.randomUUID();
    DocumentoIdentificacion documento = new DocumentoIdentificacion(documentoUnico());
    PerfilUsuario perfil =
        PerfilUsuarioFactory.crearPerfil(
            cuentaUsuarioId,
            documento,
            "Ana Perez",
            null,
            RolUsuario.CIUDADANO,
            new InformacionContacto("ana-" + cuentaUsuarioId + "@correo.com", "0999999999"),
            new CoordenadaGPS(-0.1807, -78.4678));

    adapter.guardar(perfil);

    PerfilUsuario recuperado = adapter.buscarPorId(perfil.getId()).orElseThrow();

    assertEquals(perfil.getId(), recuperado.getId());
    assertEquals(cuentaUsuarioId, recuperado.getCuentaUsuarioId());
    assertEquals(documento, recuperado.getDocumentoIdentificacion());
    assertEquals(
        "ana-" + cuentaUsuarioId + "@correo.com",
        recuperado.getInformacionContacto().getCorreoElectronico());
    assertEquals(-0.1807, recuperado.getUbicacionHabitual().getLatitud());
    assertEquals(RolUsuario.CIUDADANO, recuperado.getRol());
    assertTrue(adapter.existePorDocumentoIdentificacion(documento));
    assertTrue(adapter.existePorCuentaUsuarioId(cuentaUsuarioId));
    assertTrue(adapter.buscarPorCuentaUsuarioId(cuentaUsuarioId).isPresent());

    springDataRepository.deleteById(perfil.getId());
  }

  private String documentoUnico() {
    return String.valueOf(1700000000L + Math.floorMod(UUID.randomUUID().hashCode(), 9999999));
  }
}
