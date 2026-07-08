import { solicitarApi } from "./clienteApi";

export const sugerirPrecio = (token, { tipoResiduo, pesoKg, imagenBase64 }) =>
  solicitarApi("/sugerencias-precio", {
    metodo: "POST",
    token,
    cuerpo: { tipoResiduo, pesoKg: pesoKg ?? null, imagenBase64: imagenBase64 ?? null },
  });
