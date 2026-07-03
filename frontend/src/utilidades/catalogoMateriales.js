// Catálogo alineado al dominio del backend:
// TipoResiduo { PET, CARTON, VIDRIO, CHATARRA } y
// EstadoPublicacion { DISPONIBLE, RESERVADA, FINALIZADA, CANCELADA }.

export const TIPOS_RESIDUO = [
  { value: "PET", label: "Plástico PET" },
  { value: "CARTON", label: "Cartón" },
  { value: "VIDRIO", label: "Vidrio" },
  { value: "CHATARRA", label: "Chatarra" },
];

export const ETIQUETAS_ESTADO_PUBLICACION = {
  DISPONIBLE: "Disponible",
  RESERVADA: "Reservada",
  FINALIZADA: "Finalizada",
  CANCELADA: "Cancelada",
};

export const etiquetaTipoResiduo = (valor) =>
  TIPOS_RESIDUO.find((tipo) => tipo.value === valor)?.label || valor;
