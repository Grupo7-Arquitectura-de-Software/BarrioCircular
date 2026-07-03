import {
  MdOutlineAnalytics,
  MdOutlineGridView,
  MdOutlineListAlt,
  MdOutlineLocalShipping,
  MdOutlineSell,
  MdOutlineSettings,
  MdOutlineShoppingCart,
  MdOutlineStorefront,
} from "react-icons/md";

// Elementos de la barra lateral por rol. Sin `ruta` el elemento se muestra
// deshabilitado (secciones aún no implementadas de los mockups).
export const NAVEGACION_CIUDADANO = [
  { etiqueta: "Mercado", icono: <MdOutlineStorefront />, ruta: "/ciudadano/mercado" },
  { etiqueta: "Panel de Control", icono: <MdOutlineGridView />, ruta: "/ciudadano/panel" },
  {
    etiqueta: "Publicaciones",
    icono: <MdOutlineListAlt />,
    ruta: "/ciudadano/publicacion-disponible",
  },
  { etiqueta: "Análisis", icono: <MdOutlineAnalytics />, ruta: "/ciudadano/analisis" },
  { etiqueta: "Configuración", icono: <MdOutlineSettings />, ruta: "/ciudadano/configuracion" },
];

export const RUTA_NUEVA_PUBLICACION_CIUDADANO = "/ciudadano/crear-publicacion";

export const NAVEGACION_CENTRO = [
  { etiqueta: "Mercado", icono: <MdOutlineStorefront />, ruta: "/centro/buscar-materiales" },
  { etiqueta: "Panel", icono: <MdOutlineGridView /> },
  { etiqueta: "Publicaciones", icono: <MdOutlineListAlt /> },
  { etiqueta: "Estadísticas", icono: <MdOutlineAnalytics /> },
  { etiqueta: "Configuración", icono: <MdOutlineSettings /> },
];

export const SUBTITULO_CENTRO = "Hub de Economía Circular";

// Navegación híbrida del reciclador: opera rutas, compra y vende.
export const NAVEGACION_RECOLECTOR = [
  { etiqueta: "Operaciones", icono: <MdOutlineLocalShipping />, ruta: "/recolector/inicio" },
  {
    etiqueta: "Comprar",
    icono: <MdOutlineShoppingCart />,
    ruta: "/recolector/ofertas-recomendadas",
  },
  { etiqueta: "Vender", icono: <MdOutlineSell />, ruta: "/recolector/vender/crear-publicacion" },
  { etiqueta: "Analíticas", icono: <MdOutlineAnalytics /> },
  { etiqueta: "Configuración", icono: <MdOutlineSettings /> },
];

export const SUBTITULO_RECOLECTOR = "Comunidad Circular";
