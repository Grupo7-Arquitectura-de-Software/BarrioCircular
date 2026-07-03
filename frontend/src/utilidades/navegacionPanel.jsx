import {
  MdOutlineAnalytics,
  MdOutlineGridView,
  MdOutlineListAlt,
  MdOutlineSettings,
  MdOutlineStorefront,
} from "react-icons/md";

// Elementos de la barra lateral por rol. Sin `ruta` el elemento se muestra
// deshabilitado (secciones aún no implementadas de los mockups).
export const NAVEGACION_CIUDADANO = [
  { etiqueta: "Mercado", icono: <MdOutlineStorefront /> },
  { etiqueta: "Panel de Control", icono: <MdOutlineGridView />, ruta: "/ciudadano/panel" },
  {
    etiqueta: "Publicaciones",
    icono: <MdOutlineListAlt />,
    ruta: "/ciudadano/publicacion-disponible",
  },
  { etiqueta: "Análisis", icono: <MdOutlineAnalytics /> },
  { etiqueta: "Configuración", icono: <MdOutlineSettings /> },
];

export const RUTA_NUEVA_PUBLICACION_CIUDADANO = "/ciudadano/crear-publicacion";
