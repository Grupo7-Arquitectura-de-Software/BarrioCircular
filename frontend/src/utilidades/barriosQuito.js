// Bounding box de Quito que exige el backend para las coordenadas de una
// publicación (lat [-0.50, 0.10], lon [-78.70, -78.20]).
export const LIMITES_QUITO = {
  latitudMinima: -0.5,
  latitudMaxima: 0.1,
  longitudMinima: -78.7,
  longitudMaxima: -78.2,
};

export const estaDentroDeQuito = (latitud, longitud) =>
  latitud >= LIMITES_QUITO.latitudMinima &&
  latitud <= LIMITES_QUITO.latitudMaxima &&
  longitud >= LIMITES_QUITO.longitudMinima &&
  longitud <= LIMITES_QUITO.longitudMaxima;

// Barrios de referencia de Quito con coordenadas representativas, dentro del
// bounding box que exige el backend (lat [-0.50, 0.10], lon [-78.70, -78.20]).
// Cobertura general por zonas (norte, centro, sur y valles) sin ser exhaustiva.
export const BARRIOS_QUITO = [
  // Norte
  { etiqueta: "Carcelén", valor: "carcelen", latitud: -0.093, longitud: -78.467 },
  { etiqueta: "El Condado", valor: "el_condado", latitud: -0.085, longitud: -78.51 },
  { etiqueta: "Calderón", valor: "calderon", latitud: -0.096, longitud: -78.421 },
  { etiqueta: "Cotocollao", valor: "cotocollao", latitud: -0.106, longitud: -78.497 },
  // Centro-norte
  { etiqueta: "La Carolina", valor: "la_carolina", latitud: -0.183, longitud: -78.483 },
  { etiqueta: "La Mariscal", valor: "la_mariscal", latitud: -0.203, longitud: -78.49 },
  { etiqueta: "La Floresta", valor: "la_floresta", latitud: -0.205, longitud: -78.483 },
  // Centro
  { etiqueta: "Centro Histórico", valor: "centro_historico", latitud: -0.22, longitud: -78.513 },
  // Sur
  { etiqueta: "Solanda", valor: "solanda", latitud: -0.27, longitud: -78.537 },
  { etiqueta: "Chillogallo", valor: "chillogallo", latitud: -0.295, longitud: -78.565 },
  { etiqueta: "Quitumbe", valor: "quitumbe", latitud: -0.296, longitud: -78.548 },
  { etiqueta: "Guamaní", valor: "guamani", latitud: -0.337, longitud: -78.554 },
  // Valles (este)
  { etiqueta: "Cumbayá", valor: "cumbaya", latitud: -0.201, longitud: -78.426 },
  { etiqueta: "Tumbaco", valor: "tumbaco", latitud: -0.207, longitud: -78.4 },
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
