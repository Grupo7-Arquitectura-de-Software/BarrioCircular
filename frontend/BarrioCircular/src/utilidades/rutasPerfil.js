const RUTAS_PRINCIPALES_POR_ROL = Object.freeze({
  CIUDADANO: "/ciudadano/crear-publicacion",
  RECICLADOR: "/recolector/inicio",
  CENTRO_RECOLECCION: "/centro/buscar-materiales",
});

const PREFIJOS_POR_ROL = Object.freeze({
  CIUDADANO: "/ciudadano",
  RECICLADOR: "/recolector",
  CENTRO_RECOLECCION: "/centro",
});

export const obtenerRutaPrincipalPorRol = (rol) => RUTAS_PRINCIPALES_POR_ROL[rol] || null;

export const estaEnAreaDeRol = (rutaActual, rol) => {
  const prefijoPermitido = PREFIJOS_POR_ROL[rol];
  return Boolean(prefijoPermitido && rutaActual.startsWith(prefijoPermitido));
};
