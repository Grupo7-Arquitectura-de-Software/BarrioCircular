import { solicitarApi } from "./clienteApi";

export const registrarSesion = (token) =>
  solicitarApi("/acceso/sesion", {
    metodo: "POST",
    token,
    cuerpo: { tokenClerk: token },
  });
