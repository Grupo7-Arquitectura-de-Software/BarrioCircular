package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barriocircular.backend.publicacion.aplicacion.comandos.ReservarPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.aplicacion.excepciones.PublicacionNoEncontradaException;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;

@Service
public class ReservarPublicacionUseCase {

    private final PublicacionRepositorio publicacionRepositorio;

    public ReservarPublicacionUseCase(PublicacionRepositorio publicacionRepositorio) {
        this.publicacionRepositorio = publicacionRepositorio;
    }

    @Transactional
    public PublicacionResultado ejecutar(ReservarPublicacionCommand command) {
        PublicacionId id = PublicacionId.de(command.publicacionId());

        Publicacion publicacion = publicacionRepositorio.buscarPorId(id)
                .orElseThrow(PublicacionNoEncontradaException::new);

        publicacion.reservarPublicacionPor(ReservadorId.de(command.reservadorId()));

        publicacionRepositorio.guardar(publicacion);

        return convertirResultado(publicacion);
    }

    private PublicacionResultado convertirResultado(Publicacion publicacion) {
        return new PublicacionResultado(
                publicacion.id().valor(),
                publicacion.creador().valor(),
                publicacion.detalle().tipo().name(),
                publicacion.detalle().peso().valorKg(),
                publicacion.precioPorKilo().valor(),
                publicacion.ubicacion().latitud(),
                publicacion.ubicacion().longitud(),
                publicacion.evidencia().url(),
                publicacion.estado().name(),
                publicacion.fechaCreacion(),
                publicacion.reservadoPor() == null ? null : publicacion.reservadoPor().valor());
    }
}
