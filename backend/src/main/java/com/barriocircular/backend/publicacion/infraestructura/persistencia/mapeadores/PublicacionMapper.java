package com.barriocircular.backend.publicacion.infraestructura.persistencia.mapeadores;

import org.springframework.stereotype.Component;

import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.DetalleMaterial;
import com.barriocircular.backend.publicacion.dominio.modelo.EstadoPublicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.EvidenciaVisual;
import com.barriocircular.backend.publicacion.dominio.modelo.PesoEstimado;
import com.barriocircular.backend.publicacion.dominio.modelo.PrecioPorKilo;
import com.barriocircular.backend.publicacion.dominio.modelo.Publicacion;
import com.barriocircular.backend.publicacion.dominio.modelo.PublicacionId;
import com.barriocircular.backend.publicacion.dominio.modelo.ReservadorId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import com.barriocircular.backend.publicacion.dominio.modelo.UbicacionRecogida;
import com.barriocircular.backend.publicacion.infraestructura.persistencia.jpa.PublicacionEntity;

@Component
public class PublicacionMapper {

    public PublicacionEntity toEntity(Publicacion publicacion) {
        return new PublicacionEntity(
                publicacion.id().valor(),
                publicacion.creador().valor(),
                publicacion.detalle().tipo().name(),
                publicacion.detalle().peso().valorKg(),
                publicacion.precioPorKilo().valor(),
                publicacion.ubicacion().latitud(),
                publicacion.ubicacion().longitud(),
                publicacion.evidencia().url(),
                publicacion.fechaCreacion(),
                publicacion.estado().name(),
                publicacion.reservadoPor() == null ? null : publicacion.reservadoPor().valor());
    }

    public Publicacion toDomain(PublicacionEntity entity) {
        DetalleMaterial detalle = new DetalleMaterial(
                TipoResiduo.valueOf(entity.getTipoResiduo()),
                PesoEstimado.deKilos(entity.getPesoKg()));

        ReservadorId reservadoPor = entity.getReservadoPor() == null
                ? null
                : ReservadorId.de(entity.getReservadoPor());

        return Publicacion.reconstituir(
                PublicacionId.de(entity.getId()),
                CiudadanoId.de(entity.getCreadorId()),
                detalle,
                new PrecioPorKilo(entity.getPrecioPorKilo()),
                new UbicacionRecogida(entity.getLatitud(), entity.getLongitud()),
                new EvidenciaVisual(entity.getEvidenciaUrl()),
                entity.getFechaCreacion(),
                EstadoPublicacion.valueOf(entity.getEstado()),
                reservadoPor);
    }
}
