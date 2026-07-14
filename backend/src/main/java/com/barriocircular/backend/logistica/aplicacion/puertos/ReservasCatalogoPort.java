package com.barriocircular.backend.logistica.aplicacion.puertos;

import com.barriocircular.backend.logistica.aplicacion.dto.ReservaCatalogo;
import java.util.List;
import java.util.UUID;

public interface ReservasCatalogoPort {

  List<ReservaCatalogo> obtenerReservasActivasPorReciclador(UUID recicladorId);
}
