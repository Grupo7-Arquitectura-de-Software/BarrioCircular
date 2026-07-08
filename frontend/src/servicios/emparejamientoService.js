import { solicitarApi } from "./clienteApi";

export const buscarOfertasOptimas = (token, filtros) =>
  solicitarApi("/emparejamiento/buscar", {
    metodo: "POST",
    token,
    cuerpo: filtros,
  });
