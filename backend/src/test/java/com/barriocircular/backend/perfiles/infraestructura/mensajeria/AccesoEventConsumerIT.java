package com.barriocircular.backend.perfiles.infraestructura.mensajeria;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.barriocircular.backend.acceso.dominio.eventos.UsuarioRegistrado;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilOnboardingPendienteRepository;
import com.barriocircular.backend.perfiles.infraestructura.persistencia.jpa.SpringDataPerfilUsuarioRepository;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@ActiveProfiles("supabase")
@EnabledIfEnvironmentVariable(named = "BARRIO_CIRCULAR_RUN_DB_IT", matches = "true")
class AccesoEventConsumerIT {

    @Autowired
    private AccesoEventConsumer consumer;

    @Autowired
    private SpringDataPerfilOnboardingPendienteRepository onboardingRepository;

    @Autowired
    private SpringDataPerfilUsuarioRepository perfilUsuarioRepository;

    @Test
    void registraOnboardingPendienteSinCrearPerfilUsuarioNiDuplicar() {
        UUID cuentaId = UUID.randomUUID();
        UsuarioRegistrado evento = new UsuarioRegistrado(
                cuentaId,
                "clerk-" + cuentaId,
                "ana-" + cuentaId + "@correo.com",
                Instant.now());

        consumer.alRegistrarUsuario(evento);
        consumer.alRegistrarUsuario(evento);

        assertTrue(onboardingRepository.findByCuentaId(cuentaId).isPresent());
        assertFalse(perfilUsuarioRepository.findByCuentaUsuarioId(cuentaId).isPresent());

        onboardingRepository.findByCuentaId(cuentaId)
                .ifPresent(onboarding -> onboardingRepository.deleteById(onboarding.getId()));
    }
}
