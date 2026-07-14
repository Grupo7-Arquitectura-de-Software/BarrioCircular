package com.barriocircular.backend.perfiles.infraestructura.persistencia.adaptadores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.perfiles.dominio.factories.PerfilUsuarioFactory;
import com.barriocircular.backend.perfiles.dominio.modelo.PerfilUsuario;
import com.barriocircular.backend.perfiles.dominio.modelo.RolUsuario;
import com.barriocircular.backend.perfiles.dominio.valueobjects.CoordenadaGPS;
import com.barriocircular.backend.perfiles.dominio.valueobjects.DocumentoIdentificacion;
import com.barriocircular.backend.perfiles.dominio.valueobjects.InformacionContacto;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.PerfilUsuarioEntity;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilUsuarioRepository;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.mapeadores.PerfilUsuarioMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PerfilUsuarioRepositoryAdapterTest {

  @Test
  void guardarYBuscarPorCuentaUsuarioIdConvierteEntreDominioYEntidad() {
    SpringDataPerfilUsuarioRepository springDataRepository =
        org.mockito.Mockito.mock(SpringDataPerfilUsuarioRepository.class);
    PerfilUsuarioMapper mapper = new PerfilUsuarioMapper();
    PerfilUsuarioRepositoryAdapter adapter =
        new PerfilUsuarioRepositoryAdapter(springDataRepository, mapper);
    PerfilUsuario perfil =
        PerfilUsuarioFactory.crearPerfil(
            UUID.randomUUID(),
            new DocumentoIdentificacion("1712345678"),
            "Ana Perez",
            null,
            RolUsuario.CIUDADANO,
            new InformacionContacto("ana@correo.com", "0999999999"),
            new CoordenadaGPS(-0.1807, -78.4678));

    adapter.guardar(perfil);

    ArgumentCaptor<PerfilUsuarioEntity> captor = ArgumentCaptor.forClass(PerfilUsuarioEntity.class);
    verify(springDataRepository).save(captor.capture());
    when(springDataRepository.findByCuentaUsuarioId(perfil.getCuentaUsuarioId()))
        .thenReturn(Optional.of(captor.getValue()));

    Optional<PerfilUsuario> recuperado =
        adapter.buscarPorCuentaUsuarioId(perfil.getCuentaUsuarioId());

    assertTrue(recuperado.isPresent());
    assertEquals(perfil.getId(), recuperado.get().getId());
    assertTrue(recuperado.get().obtenerEventosDominio().isEmpty());
    verify(springDataRepository).findByCuentaUsuarioId(perfil.getCuentaUsuarioId());
    verify(springDataRepository).save(any(PerfilUsuarioEntity.class));
  }
}
