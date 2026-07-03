// Datos de ejemplo del mercado, alineados al dominio del backend
// (TipoResiduo / campos de la entidad Publicacion). Se reemplazarán por el
// API de publicaciones cuando el backend exponga los endpoints.

export const MATERIALES_RECOMENDADOS = [
  {
    id: 1,
    tipoResiduo: "PET",
    titulo: "Botellas PET Transparentes Premium",
    precioPorKilo: "$0.45",
    pesoKg: 120,
    ubicacion: "Cumbayá",
    distanciaKm: 3.2,
    descripcion:
      "Limpiadas, trituradas y embaladas. Listas para recogida inmediata. Procedentes de restaurantes...",
    puntuacion: 94,
  },
  {
    id: 2,
    tipoResiduo: "CARTON",
    titulo: "Cartón Corrugado Limpio",
    precioPorKilo: "$0.18",
    pesoKg: 450,
    ubicacion: "La Carolina",
    distanciaKm: 6.5,
    descripcion:
      "Aplanado y flejado. Gran volumen disponible semanalmente de un colectivo minorista local.",
    puntuacion: 82,
  },
];
