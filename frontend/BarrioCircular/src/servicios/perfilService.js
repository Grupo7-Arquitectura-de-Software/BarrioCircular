import { solicitarApi } from "./clienteApi";

export const obtenerMiPerfil = (token) => solicitarApi("/perfiles/me", { token });

export const completarPerfil = (token, datosPerfil) => solicitarApi("/perfiles/completar", {
    metodo: "POST",
    token,
    cuerpo: datosPerfil,
});
