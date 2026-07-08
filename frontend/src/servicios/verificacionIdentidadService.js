import { solicitarApi } from "./clienteApi";

export const emitirCredencialIdentidad = (token) =>
  solicitarApi("/verificacion-identidad/credenciales", {
    metodo: "POST",
    token,
  });

export const verificarCredencialPublica = (tokenVerificacion) =>
  solicitarApi(`/verificacion-identidad/publico/${encodeURIComponent(tokenVerificacion)}`);
