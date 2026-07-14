import {
  MdOutlineGridView,
  MdOutlineListAlt,
  MdOutlineLocalShipping,
  MdOutlineMap,
  MdOutlineQrCode2,
  MdOutlineSell,
  MdOutlineSettings,
  MdOutlineShoppingCart,
  MdOutlineStorefront,
} from "react-icons/md";

// Elementos de la barra lateral por rol, alineados a los casos de uso
// documentados (05.1). Sin `ruta` el elemento se muestra deshabilitado.
export const NAVEGACION_CIUDADANO = [
  { etiqueta: "Panel de Control", icono: <MdOutlineGridView />, ruta: "/ciudadano/panel" },
  {
    etiqueta: "Publicaciones",
    icono: <MdOutlineListAlt />,
    ruta: "/ciudadano/publicacion-disponible",
  },
  { etiqueta: "Configuración", icono: <MdOutlineSettings />, ruta: "/ciudadano/configuracion" },
];

export const RUTA_NUEVA_PUBLICACION_CIUDADANO = "/ciudadano/crear-publicacion";

// El centro solo compra: explora el mercado y gestiona su perfil comercial.
export const NAVEGACION_CENTRO = [
  { etiqueta: "Mercado", icono: <MdOutlineStorefront />, ruta: "/centro/buscar-materiales" },
  { etiqueta: "Mi QR de identidad", icono: <MdOutlineQrCode2 />, ruta: "/centro/identidad" },
  { etiqueta: "Configuración", icono: <MdOutlineSettings />, ruta: "/centro/configuracion" },
];

export const SUBTITULO_CENTRO = "Hub de Economía Circular";

// Navegación híbrida del reciclador: opera rutas, compra y vende.
export const NAVEGACION_RECOLECTOR = [
  { etiqueta: "Operaciones", icono: <MdOutlineLocalShipping />, ruta: "/recolector/inicio" },
  {
    etiqueta: "Ruta de recolección",
    icono: <MdOutlineMap />,
    ruta: "/recolector/ruta-recoleccion",
  },
  {
    etiqueta: "Comprar",
    icono: <MdOutlineShoppingCart />,
    ruta: "/recolector/publicaciones-recomendadas",
  },
  { etiqueta: "Vender", icono: <MdOutlineSell />, ruta: "/recolector/vender/crear-publicacion" },
  {
    etiqueta: "Mi QR de identidad",
    icono: <MdOutlineQrCode2 />,
    ruta: "/recolector/identidad",
  },
  { etiqueta: "Configuración", icono: <MdOutlineSettings />, ruta: "/recolector/configuracion" },
];

export const SUBTITULO_RECOLECTOR = "Comunidad Circular";
