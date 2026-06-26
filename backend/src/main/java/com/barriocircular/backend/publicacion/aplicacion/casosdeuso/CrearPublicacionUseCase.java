package com.barriocircular.backend.publicacion.aplicacion.casosdeuso;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barriocircular.backend.publicacion.aplicacion.comandos.CrearPublicacionCommand;
import com.barriocircular.backend.publicacion.aplicacion.dto.PublicacionResultado;
import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.DetalleMaterial;
import com.barriocircular.backend.publicacion.dominio.modelo.EvidenciaVisual;
import com.barriocircular.backend.publicacion.dominio.modelo.PesoEstimado;
import com.barriocircular.backend.publicacion.dominio.modelo.PrecioPorKilo;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.modelo.UbicacionRecogida;
import com.barriocircular.backend.publicacion.dominio.repositorios.PublicacionRepositorio;

@Service
public class CrearPublicacionUseCase {

    private final PublicacionRepositorio publicacionRepositorio;

    public CrearPublicacionUseCase(PublicacionRepositorio publicacionRepositorio) {
        this.publicacionRepositorio = publicacionRepositorio;
    }

    @Transactional
    public PublicacionResultado ejecutar(CrearPublicacionCommand command) {
        PublicacionId id = PublicacionId.nuevo();
        CiudadanoId creador = CiudadanoId.de(command.creadorId());
        DetalleMaterial detalle = new DetalleMaterial(
                TipoResiduo.valueOf(command.tipoResiduo()),
                PesoEstimado.deKilos(command.pesoKg()));
        PrecioPorKilo precioPorKilo = new PrecioPorKilo(command.precioPorKilo());
        UbicacionRecogida ubicacion = new UbicacionRecogida(command.latitud(), command.longitud());
        EvidenciaVisual evidencia = new EvidenciaVisual(command.evidenciaUrl());

        Publicacion publicacion = Publicacion.crear(id, creador, detalle, precioPorKilo, ubicacion, evidencia);

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
