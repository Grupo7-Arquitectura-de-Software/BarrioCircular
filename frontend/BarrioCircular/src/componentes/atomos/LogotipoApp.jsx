import { Box, VStack, Text } from "@chakra-ui/react";

/**
 * Átomo: Logotipo de la aplicación BarrioCircular
 * @param {string} tamanio - sm | md | lg
 */
const LogotipoApp = ({ tamanio = "md" }) => {
    const sizes = {
        sm: { circulo: "48px", fuente: "lg", subtitulo: "xs" },
        md: { circulo: "72px", fuente: "2xl", subtitulo: "sm" },
        lg: { circulo: "96px", fuente: "4xl", subtitulo: "md" },
    };
    const s = sizes[tamanio] || sizes.md;

    return (
        <VStack gap={2} align="center">
            <Box
                width={s.circulo}
                height={s.circulo}
                borderRadius="full"
                border="3px solid"
                borderColor="gray.700"
                display="flex"
                alignItems="center"
                justifyContent="center"
                bg="white"
            >
                <Text
                    fontSize={s.fuente}
                    fontWeight="bold"
                    color="gray.700"
                    lineHeight="1"
                >
                    C
                </Text>
            </Box>
            <Text fontSize={s.subtitulo} fontWeight="semibold" color="gray.700">
                BarrioCircular
            </Text>
        </VStack>
    );
};

export default LogotipoApp;
