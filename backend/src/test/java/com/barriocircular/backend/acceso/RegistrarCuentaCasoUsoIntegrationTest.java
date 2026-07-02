package com.barriocircular.backend.acceso;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.barriocircular.backend.acceso.aplicacion.casosdeuso.RegistrarCuentaCasoUso;
import com.barriocircular.backend.acceso.aplicacion.comandos.RegistrarCuentaCommand;
import com.barriocircular.backend.acceso.aplicacion.dto.RegistrarCuentaRespuesta;
import com.barriocircular.backend.acceso.dominio.eventos.UsuarioRegistrado;
import com.barriocircular.backend.acceso.dominio.modelo.agregados.CuentaAcceso;
import com.barriocircular.backend.acceso.dominio.modelo.excepciones.CorreoDuplicadoException;
import com.barriocircular.backend.acceso.dominio.modelo.objetosValor.*;
import com.barriocircular.backend.acceso.dominio.repositorios.CuentaAccesoRepositorio;
import com.barriocircular.backend.acceso.dominio.servicios.ValidadorIdentidad;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
class RegistrarCuentaCasoUsoIntegrationTest {

  @Autowired private RegistrarCuentaCasoUso casoUso;

  @Autowired private CuentaAccesoRepositorio repositorio;

  @MockitoBean private ValidadorIdentidad validadorIdentidad;

  @Autowired private ApplicationEvents eventos;

  @Test
  void deberiaRegistrarNuevaCuentaYPublicarEventoExactamenteUnaVez() {
    when(validadorIdentidad.validarUsuario(any()))
        .thenReturn(
            new DatosUsuarioVerificado(
                new IdentificadorUsuarioClerk("user_3FTUlOTuE2Y5X1PGdprm3fKHOL7"),
                new CorreoElectronico("af6726991@gmail.com"),
                true));

    RegistrarCuentaRespuesta respuesta =
        casoUso.ejecutar(
            new RegistrarCuentaCommand(
                "eyJhbGciOiJSUzI1NiIsImNhdCI6ImNsX0I3ZDRQRDIyMkFBQSIsImtpZCI6Imluc18zRlFXcTY2OWRiVnFNaXJpcWJuMEtmRG9mbU4iLCJ0eXAiOiJKV1QifQ.eyJlbWFpbCI6ImFmNjcyNjk5MUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6Int7dXNlci5wcmltYXJ5X2VtYWlsX2FkZHJlc3NfdmVyaWZpZWR9fSIsImV4cCI6MTc4MjI1MjM0NiwiaWF0IjoxNzgyMjUxODQ2LCJpc3MiOiJodHRwczovL2NvbW11bmFsLWNyYWItNDguY2xlcmsuYWNjb3VudHMuZGV2IiwianRpIjoiYzc4ZGQyYzViOWZlZDhhNGNjMjAiLCJuYmYiOjE3ODIyNTE4NDEsInN1YiI6InVzZXJfM0ZUVWxPVHVFMlk1WDFQR2Rwcm0zZktIT0w3In0.dSkqFC8pxwABdEsqB3-AhydlMdSUBUkVUa7PSaBFDj54HYmpx4paUzOwzb3_8w3craMsKtXyvEnb3cRfxmAVhF5FthzrjtLdVz45XL1LwFgxyq93oBRKY8bflCwCsPn4S8lZ6525UvmwADhyn_lsb1GZU8AFMu0ktZU8XMt8IMTXJjQOoVUzj-7zczsYwemPNL7CQ2io4rwYnXd-5TupVhC4uq9hupxOGFCNrS35XDJDZfu8ZaOGp0qRdJiKhqnDD39nGv9YRVy_aQxc7OtZ-h8aj5nK9vtdbLy5VjGLb7xqzkKT2zyFwKAb1Udf0vhQ7OP_iXTNkyDoSQg1KMwTbw"));

    assertThat(respuesta.esNueva()).isTrue();
    assertThat(respuesta.estado()).isEqualTo(EstadoSesion.ACTIVA);

    Optional<CuentaAcceso> cuenta =
        repositorio.buscarPorClerkId("user_3FTUlOTuE2Y5X1PGdprm3fKHOL7");
    assertThat(cuenta).isPresent();
    assertThat(cuenta.get().getCorreoElectronico().correoElectronico())
        .isEqualTo("af6726991@gmail.com");

    assertThat(eventos.stream(UsuarioRegistrado.class).count()).isEqualTo(1);

    UsuarioRegistrado evento = eventos.stream(UsuarioRegistrado.class).findFirst().orElseThrow();
    assertThat(evento.clerkId()).isEqualTo("user_3FTUlOTuE2Y5X1PGdprm3fKHOL7");
    assertThat(evento.correoElectronico()).isEqualTo("af6726991@gmail.com");
  }

  @Test
  void segundoIntentoConMismoClerkIdNoDuplicaCuentaNiEvento() {
    when(validadorIdentidad.validarUsuario(any()))
        .thenReturn(
            new DatosUsuarioVerificado(
                new IdentificadorUsuarioClerk("user_456"),
                new CorreoElectronico("repetido@example.com"),
                true));

    RegistrarCuentaRespuesta primera =
        casoUso.ejecutar(
            new RegistrarCuentaCommand(
                "eyJhbGciOiJSUzI1NiIsImNhdCI6ImNsX0I3ZDRQRDIyMkFBQSIsImtpZCI6Imluc18zRlFXcTY2OWRiVnFNaXJpcWJuMEtmRG9mbU4iLCJ0eXAiOiJKV1QifQ.eyJlbWFpbCI6ImFmNjcyNjk5MUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6Int7dXNlci5wcmltYXJ5X2VtYWlsX2FkZHJlc3NfdmVyaWZpZWR9fSIsImV4cCI6MTc4MjI1MjM0NiwiaWF0IjoxNzgyMjUxODQ2LCJpc3MiOiJodHRwczovL2NvbW11bmFsLWNyYWItNDguY2xlcmsuYWNjb3VudHMuZGV2IiwianRpIjoiYzc4ZGQyYzViOWZlZDhhNGNjMjAiLCJuYmYiOjE3ODIyNTE4NDEsInN1YiI6InVzZXJfM0ZUVWxPVHVFMlk1WDFQR2Rwcm0zZktIT0w3In0.dSkqFC8pxwABdEsqB3-AhydlMdSUBUkVUa7PSaBFDj54HYmpx4paUzOwzb3_8w3craMsKtXyvEnb3cRfxmAVhF5FthzrjtLdVz45XL1LwFgxyq93oBRKY8bflCwCsPn4S8lZ6525UvmwADhyn_lsb1GZU8AFMu0ktZU8XMt8IMTXJjQOoVUzj-7zczsYwemPNL7CQ2io4rwYnXd-5TupVhC4uq9hupxOGFCNrS35XDJDZfu8ZaOGp0qRdJiKhqnDD39nGv9YRVy_aQxc7OtZ-h8aj5nK9vtdbLy5VjGLb7xqzkKT2zyFwKAb1Udf0vhQ7OP_iXTNkyDoSQg1KMwTbw"));
    RegistrarCuentaRespuesta segunda =
        casoUso.ejecutar(
            new RegistrarCuentaCommand(
                "eyJhbGciOiJSUzI1NiIsImNhdCI6ImNsX0I3ZDRQRDIyMkFBQSIsImtpZCI6Imluc18zRlFXcTY2OWRiVnFNaXJpcWJuMEtmRG9mbU4iLCJ0eXAiOiJKV1QifQ.eyJlbWFpbCI6ImFmNjcyNjk5MUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6Int7dXNlci5wcmltYXJ5X2VtYWlsX2FkZHJlc3NfdmVyaWZpZWR9fSIsImV4cCI6MTc4MjI1MjM0NiwiaWF0IjoxNzgyMjUxODQ2LCJpc3MiOiJodHRwczovL2NvbW11bmFsLWNyYWItNDguY2xlcmsuYWNjb3VudHMuZGV2IiwianRpIjoiYzc4ZGQyYzViOWZlZDhhNGNjMjAiLCJuYmYiOjE3ODIyNTE4NDEsInN1YiI6InVzZXJfM0ZUVWxPVHVFMlk1WDFQR2Rwcm0zZktIT0w3In0.dSkqFC8pxwABdEsqB3-AhydlMdSUBUkVUa7PSaBFDj54HYmpx4paUzOwzb3_8w3craMsKtXyvEnb3cRfxmAVhF5FthzrjtLdVz45XL1LwFgxyq93oBRKY8bflCwCsPn4S8lZ6525UvmwADhyn_lsb1GZU8AFMu0ktZU8XMt8IMTXJjQOoVUzj-7zczsYwemPNL7CQ2io4rwYnXd-5TupVhC4uq9hupxOGFCNrS35XDJDZfu8ZaOGp0qRdJiKhqnDD39nGv9YRVy_aQxc7OtZ-h8aj5nK9vtdbLy5VjGLb7xqzkKT2zyFwKAb1Udf0vhQ7OP_iXTNkyDoSQg1KMwTbw"));

    assertThat(primera.esNueva()).isTrue();
    assertThat(segunda.esNueva()).isFalse();
    assertThat(segunda.cuentaId()).isEqualTo(primera.cuentaId());

    assertThat(repositorio.buscarPorClerkId("user_456")).isPresent();
    // El evento se publicó solo en el primer registro
    assertThat(eventos.stream(UsuarioRegistrado.class).count()).isEqualTo(1);
  }

  @Test
  void correoDuplicadoConDistintoClerkIdLanzaExcepcionYNoPersiste() {
    when(validadorIdentidad.validarUsuario(any()))
        .thenReturn(
            new DatosUsuarioVerificado(
                new IdentificadorUsuarioClerk("user_original"),
                new CorreoElectronico("af6726991@gmail.com"),
                true));
    casoUso.ejecutar(
        new RegistrarCuentaCommand(
            "eyJhbGciOiJSUzI1NiIsImNhdCI6ImNsX0I3ZDRQRDIyMkFBQSIsImtpZCI6Imluc18zRlFXcTY2OWRiVnFNaXJpcWJuMEtmRG9mbU4iLCJ0eXAiOiJKV1QifQ.eyJlbWFpbCI6ImFmNjcyNjk5MUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6Int7dXNlci5wcmltYXJ5X2VtYWlsX2FkZHJlc3NfdmVyaWZpZWR9fSIsImV4cCI6MTc4MjI1MjM0NiwiaWF0IjoxNzgyMjUxODQ2LCJpc3MiOiJodHRwczovL2NvbW11bmFsLWNyYWItNDguY2xlcmsuYWNjb3VudHMuZGV2IiwianRpIjoiYzc4ZGQyYzViOWZlZDhhNGNjMjAiLCJuYmYiOjE3ODIyNTE4NDEsInN1YiI6InVzZXJfM0ZUVWxPVHVFMlk1WDFQR2Rwcm0zZktIT0w3In0.dSkqFC8pxwABdEsqB3-AhydlMdSUBUkVUa7PSaBFDj54HYmpx4paUzOwzb3_8w3craMsKtXyvEnb3cRfxmAVhF5FthzrjtLdVz45XL1LwFgxyq93oBRKY8bflCwCsPn4S8lZ6525UvmwADhyn_lsb1GZU8AFMu0ktZU8XMt8IMTXJjQOoVUzj-7zczsYwemPNL7CQ2io4rwYnXd-5TupVhC4uq9hupxOGFCNrS35XDJDZfu8ZaOGp0qRdJiKhqnDD39nGv9YRVy_aQxc7OtZ-h8aj5nK9vtdbLy5VjGLb7xqzkKT2zyFwKAb1Udf0vhQ7OP_iXTNkyDoSQg1KMwTbw"));

    when(validadorIdentidad.validarUsuario(any()))
        .thenReturn(
            new DatosUsuarioVerificado(
                new IdentificadorUsuarioClerk("user_3FTUlOTuE2Y5X1PGdprm3fKHOL7"),
                new CorreoElectronico("af6726991@gmail.com"),
                true));

    assertThatThrownBy(
            () ->
                casoUso.ejecutar(
                    new RegistrarCuentaCommand(
                        "eyJhbGciOiJSUzI1NiIsImNhdCI6ImNsX0I3ZDRQRDIyMkFBQSIsImtpZCI6Imluc18zRlFXcTY2OWRiVnFNaXJpcWJuMEtmRG9mbU4iLCJ0eXAiOiJKV1QifQ.eyJlbWFpbCI6ImFmNjcyNjk5MUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6Int7dXNlci5wcmltYXJ5X2VtYWlsX2FkZHJlc3NfdmVyaWZpZWR9fSIsImV4cCI6MTc4MjI1MjM0NiwiaWF0IjoxNzgyMjUxODQ2LCJpc3MiOiJodHRwczovL2NvbW11bmFsLWNyYWItNDguY2xlcmsuYWNjb3VudHMuZGV2IiwianRpIjoiYzc4ZGQyYzViOWZlZDhhNGNjMjAiLCJuYmYiOjE3ODIyNTE4NDEsInN1YiI6InVzZXJfM0ZUVWxPVHVFMlk1WDFQR2Rwcm0zZktIT0w3In0.dSkqFC8pxwABdEsqB3-AhydlMdSUBUkVUa7PSaBFDj54HYmpx4paUzOwzb3_8w3craMsKtXyvEnb3cRfxmAVhF5FthzrjtLdVz45XL1LwFgxyq93oBRKY8bflCwCsPn4S8lZ6525UvmwADhyn_lsb1GZU8AFMu0ktZU8XMt8IMTXJjQOoVUzj-7zczsYwemPNL7CQ2io4rwYnXd-5TupVhC4uq9hupxOGFCNrS35XDJDZfu8ZaOGp0qRdJiKhqnDD39nGv9YRVy_aQxc7OtZ-h8aj5nK9vtdbLy5VjGLb7xqzkKT2zyFwKAb1Udf0vhQ7OP_iXTNkyDoSQg1KMwTbw")))
        .isInstanceOf(CorreoDuplicadoException.class);

    assertThat(repositorio.buscarPorClerkId("user_3FTUlOTuE2Y5X1PGdprm3fKHOL7")).isEmpty();
  }
}
