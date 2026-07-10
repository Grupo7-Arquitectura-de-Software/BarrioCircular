import { solicitarApi } from "./clienteApi";

export const obtenerRutaActiva = (token) => solicitarApi("/logistica/rutas/activa", { token });

export const obtenerRutaPorId = (token, rutaId) =>
  solicitarApi(`/logistica/rutas/${rutaId}`, { token });

export const construirRuta = (token, datos) =>
  solicitarApi("/logistica/rutas", {
    metodo: "POST",
    token,
    cuerpo: datos,
  });

export const actualizarRutaActiva = (token) =>
  solicitarApi("/logistica/rutas/activa", {
    metodo: "PUT",
    token,
  });

export const iniciarRutaActiva = (token) =>
  solicitarApi("/logistica/rutas/activa/iniciar", {
    metodo: "POST",
    token,
  });

export const registrarLlegadaParada = (token, rutaId, paradaId, datos) =>
  solicitarApi(`/logistica/rutas/${rutaId}/paradas/${paradaId}/llegada`, {
    metodo: "PATCH",
    token,
    cuerpo: datos,
  });
