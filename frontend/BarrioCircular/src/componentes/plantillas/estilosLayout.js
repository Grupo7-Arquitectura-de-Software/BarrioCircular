export const fondoPagina = {
    minH: "100vh",
    w: "100%",
    bg: "gray.50",
};

export const contenedorApp = {
    w: "100%",
    maxW: { base: "100%", md: "768px", lg: "960px", xl: "1200px" },
    mx: "auto",
    minH: "100vh",
    bg: "white",
    display: "flex",
    flexDirection: "column",
    boxShadow: { md: "md" },
    borderX: { md: "1px solid" },
    borderColor: { md: "gray.200" },
};

export const contenedorAuthTarjeta = {
    w: "100%",
    maxW: { base: "100%", sm: "440px", md: "480px" },
    bg: "white",
    borderRadius: { md: "xl" },
    border: { md: "1px solid" },
    borderColor: { md: "gray.200" },
    p: { base: 4, md: 8 },
    boxShadow: { md: "lg" },
};

export const paddingContenido = {
    p: { base: 4, md: 6, lg: 8 },
};

export const ETIQUETAS_ROL = {
    ciudadano: "Ciudadano / Vendedor",
    recolector: "Reciclador",
    centro: "Centro de Recolección",
};

export const RUTAS_INICIO_ROL = {
    ciudadano: "/ciudadano/crear-publicacion",
    recolector: "/recolector/inicio",
    centro: "/centro/buscar-materiales",
};
