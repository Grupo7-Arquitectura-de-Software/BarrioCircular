import { createSystem, defaultConfig, defineConfig } from "@chakra-ui/react";

// Tokens del Sistema de Diseño BarrioCircular (Entregable 1).
const config = defineConfig({
  globalCss: {
    body: {
      bg: "superficie.brillante",
      color: "gray.900",
    },
  },
  theme: {
    tokens: {
      colors: {
        verde: {
          50: { value: "#e6f4ec" },
          100: { value: "#c3e5d1" },
          200: { value: "#9cd5b4" },
          300: { value: "#6fc496" },
          400: { value: "#3aa872" },
          500: { value: "#04864b" }, // bg-primary-container
          600: { value: "#006a3a" }, // bg-primary
          700: { value: "#00552e" },
          800: { value: "#004023" },
          900: { value: "#002b17" },
        },
        azul: {
          50: { value: "#e8eeff" },
          100: { value: "#c7d6fc" },
          200: { value: "#9db6f8" },
          300: { value: "#7396f5" },
          400: { value: "#316bf3" }, // bg-secondary-container
          500: { value: "#0051d5" }, // bg-secondary
          600: { value: "#0041ab" },
          700: { value: "#003181" },
          800: { value: "#002157" },
          900: { value: "#00112e" },
        },
        superficie: {
          brillante: { value: "#faf8ff" }, // bg-surface-bright
          contenedor: { value: "#eaedff" }, // bg-surface-container
        },
        rojoError: { value: "#ba1a1a" }, // bg-error
        contorno: { value: "#6e7a6f" }, // bg-outline
      },
      fonts: {
        heading: { value: "'Plus Jakarta Sans Variable', 'Plus Jakarta Sans', sans-serif" },
        body: { value: "'Inter', sans-serif" },
      },
    },
    semanticTokens: {
      colors: {
        // Paletas para colorPalette="verde" / "azul" en Chakra v3.
        verde: {
          solid: { value: "{colors.verde.600}" },
          contrast: { value: "white" },
          fg: { value: "{colors.verde.700}" },
          muted: { value: "{colors.verde.100}" },
          subtle: { value: "{colors.verde.50}" },
          emphasized: { value: "{colors.verde.200}" },
          focusRing: { value: "{colors.verde.600}" },
        },
        azul: {
          solid: { value: "{colors.azul.500}" },
          contrast: { value: "white" },
          fg: { value: "{colors.azul.600}" },
          muted: { value: "{colors.azul.100}" },
          subtle: { value: "{colors.azul.50}" },
          emphasized: { value: "{colors.azul.200}" },
          focusRing: { value: "{colors.azul.500}" },
        },
        fondo: {
          pagina: { value: "{colors.superficie.brillante}" },
          tarjeta: { value: "white" },
          cabeceraTarjeta: { value: "{colors.superficie.contenedor}" },
        },
        marca: {
          primario: { value: "{colors.verde.600}" },
          primarioContenedor: { value: "{colors.verde.500}" },
          secundario: { value: "{colors.azul.500}" },
          error: { value: "{colors.rojoError}" },
          contorno: { value: "{colors.contorno}" },
        },
      },
    },
  },
});

export const system = createSystem(defaultConfig, config);
