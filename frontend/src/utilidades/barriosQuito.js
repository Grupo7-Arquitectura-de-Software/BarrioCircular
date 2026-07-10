// Barrios de Quito con coordenadas representativas, dentro del bounding box
// que exige el backend (lat [-0.50, 0.10], lon [-78.70, -78.20]).
export const BARRIOS_QUITO = [
  { etiqueta: "La Floresta", valor: "la_floresta", latitud: -0.205, longitud: -78.483 },
  { etiqueta: "Cumbayá", valor: "cumbaya", latitud: -0.201, longitud: -78.426 },
  { etiqueta: "La Carolina", valor: "la_carolina", latitud: -0.183, longitud: -78.483 },
  { etiqueta: "La Mariscal", valor: "la_mariscal", latitud: -0.203, longitud: -78.49 },
  { etiqueta: "Centro Histórico", valor: "centro_historico", latitud: -0.22, longitud: -78.513 },
  { etiqueta: "Quitumbe", valor: "quitumbe", latitud: -0.296, longitud: -78.548 },
];

export const obtenerCoordenadasDeBarrio = (valorBarrio) => {
  const barrio = BARRIOS_QUITO.find((candidato) => candidato.valor === valorBarrio);
  return barrio ? { latitud: barrio.latitud, longitud: barrio.longitud } : null;
};

const buscarBarrioMasCercano = (latitud, longitud) => {
  let masCercano = null;
  let menorDistancia = Infinity;
  for (const barrio of BARRIOS_QUITO) {
    const distancia = (barrio.latitud - latitud) ** 2 + (barrio.longitud - longitud) ** 2;
    if (distancia < menorDistancia) {
      menorDistancia = distancia;
      masCercano = barrio;
    }
  }
  return masCercano;
};

// Ubicación legible para una publicación: el barrio del catálogo más cercano
// a sus coordenadas (aproximación euclidiana suficiente a escala de ciudad).
export const barrioMasCercano = (latitud, longitud) =>
  buscarBarrioMasCercano(latitud, longitud)?.etiqueta || "Quito";

// Valor (slug) del barrio más cercano, para preseleccionar el selector de
// ubicación al editar una publicación existente.
export const valorBarrioMasCercano = (latitud, longitud) =>
  buscarBarrioMasCercano(latitud, longitud)?.valor || "";

// Catálogo oficial del backend (enum TipoResiduo) con etiquetas de UI.
export const ETIQUETAS_TIPO_RESIDUO = {
  PET: "Plástico PET",
  CARTON: "Cartón",
  VIDRIO: "Vidrio",
  CHATARRA: "Chatarra",
};

export const etiquetaTipoResiduo = (tipo) => ETIQUETAS_TIPO_RESIDUO[tipo] || tipo;

// Etiquetas de UI para EstadoPublicacion del backend.
export const ETIQUETAS_ESTADO_PUBLICACION = {
  DISPONIBLE: "Disponible",
  RESERVADA: "Reservado",
  FINALIZADA: "Completada",
  CANCELADA: "Cancelada",
};

export const etiquetaEstadoPublicacion = (estado) => ETIQUETAS_ESTADO_PUBLICACION[estado] || estado;
