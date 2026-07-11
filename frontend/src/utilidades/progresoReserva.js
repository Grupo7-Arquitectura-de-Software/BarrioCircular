// Progreso local del flujo de una reserva (coordinar cita → verificar
// material). El backend aún no persiste la cita coordinada, así que la etapa
// se guarda en sessionStorage por publicación para que el comprador retome el
// flujo donde lo dejó al volver a sus reservas (por ejemplo, tras revisar la
// ruta de recolección).
export const ETAPAS_RESERVA = {
  COORDINAR: "COORDINAR",
  VERIFICAR: "VERIFICAR",
};

const claveEtapa = (publicacionId) => `etapa-reserva-${publicacionId}`;

export const obtenerEtapaReserva = (publicacionId) =>
  sessionStorage.getItem(claveEtapa(publicacionId)) === ETAPAS_RESERVA.VERIFICAR
    ? ETAPAS_RESERVA.VERIFICAR
    : ETAPAS_RESERVA.COORDINAR;

// Al confirmar la cita, la reserva avanza a la verificación en sitio.
export const marcarCitaConfirmada = (publicacionId) =>
  sessionStorage.setItem(claveEtapa(publicacionId), ETAPAS_RESERVA.VERIFICAR);

// Al confirmar la operación el flujo termina y la etapa guardada se descarta.
export const limpiarEtapaReserva = (publicacionId) =>
  sessionStorage.removeItem(claveEtapa(publicacionId));
