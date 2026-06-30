import { solicitarApi } from "./clienteApi";

export const obtenerMiPerfil = (token) => solicitarApi("/api/perfiles/me", { token });

export const completarPerfil = (token, datosPerfil) => solicitarApi("/api/perfiles/completar", {
    metodo: "POST",
    token,
    cuerpo: datosPerfil,
});
