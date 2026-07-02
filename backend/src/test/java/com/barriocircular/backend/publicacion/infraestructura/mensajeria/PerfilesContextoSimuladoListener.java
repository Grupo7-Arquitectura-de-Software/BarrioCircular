package com.barriocircular.backend.publicacion.infraestructura.mensajeria;

import com.barriocircular.backend.publicacion.dominio.eventos.PublicacionCreada;
import com.barriocircular.backend.publicacion.dominio.modelo.CiudadanoId;
import com.barriocircular.backend.publicacion.dominio.modelo.TipoResiduo;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.context.event.EventListener;

public class PerfilesContextoSimuladoListener {

  private final AtomicReference<PublicacionCreada> ultimoEvento = new AtomicReference<>();

  @EventListener
  public void alPublicarseUnMaterial(PublicacionCreada evento) {
    ultimoEvento.set(evento);
  }

  public boolean recibioEvento() {
    return ultimoEvento.get() != null;
  }

  public CiudadanoId creadorRecibido() {
    return ultimoEvento.get().creador();
  }

  public TipoResiduo tipoRecibido() {
    return ultimoEvento.get().tipo();
  }
}
