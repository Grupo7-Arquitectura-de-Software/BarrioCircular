import { solicitarApi } from "./clienteApi";

// Analiza la foto del material con IA (POST /api/analisis-material): valida que
// sea reciclaje, que se vea bien y que muestre un solo material; si es válida
// devuelve tipo, peso estimado, estado y precio sugerido (base de mercado x
// factor de estado). Nunca responde 5xx por fallos de la IA: en ese caso el
// resultado es "IA_NO_DISPONIBLE".
export const analizarMaterial = (token, { imagenBase64 }) =>
  solicitarApi("/analisis-material", {
    metodo: "POST",
    token,
    cuerpo: { imagenBase64 },
  });
