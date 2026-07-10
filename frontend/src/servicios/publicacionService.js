import { solicitarApi } from "./clienteApi";

export const crearPublicacion = (token, datosPublicacion) =>
  solicitarApi("/publicaciones", {
    metodo: "POST",
    token,
    cuerpo: datosPublicacion,
  });

export const obtenerPublicacionesDisponibles = (token) =>
  solicitarApi("/publicaciones/disponibles", { token });

export const obtenerMisPublicaciones = (token) => solicitarApi("/publicaciones/mias", { token });

export const obtenerPublicacion = (token, publicacionId) =>
  solicitarApi(`/publicaciones/${publicacionId}`, { token });

export const obtenerMisReservas = (token) => solicitarApi("/publicaciones/reservadas", { token });

export const reservarPublicacion = (token, publicacionId) =>
  solicitarApi(`/publicaciones/${publicacionId}/reservar`, {
    metodo: "POST",
    token,
  });

export const finalizarPublicacion = (token, publicacionId) =>
  solicitarApi(`/publicaciones/${publicacionId}/finalizar`, {
    metodo: "POST",
    token,
  });
