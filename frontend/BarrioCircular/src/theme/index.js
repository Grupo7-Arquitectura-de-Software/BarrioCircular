import {
    createSystem,
    defaultConfig,
    defineConfig
} from "@chakra-ui/react";


const config = defineConfig({

    theme: {

        // Colores personalizados
        tokens: {
            colors: {

                brand: {
                    50:  { value: "#e8f5ff" },
                    100: { value: "#cce7ff" },
                    200: { value: "#99cfff" },
                    300: { value: "#66b8ff" },
                    400: { value: "#339fff" },
                    500: { value: "#0087ff" },
                    600: { value: "#006dcc" },
                    700: { value: "#005299" },
                    800: { value: "#003766" },
                    900: { value: "#001b33" }
                }

            }
        }

    }

});


export const system = createSystem(
    defaultConfig,
    config
);