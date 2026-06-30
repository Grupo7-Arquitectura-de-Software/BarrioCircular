import { solicitarApi } from "./clienteApi";

export const registrarSesion = (token) => solicitarApi("/api/acceso/sesion", {
    metodo: "POST",
    token,
    cuerpo: { tokenClerk: token },
});
