import { VStack, Box, Text } from "@chakra-ui/react";
import { LuClock } from "react-icons/lu";

const PanelEstadoOferta = ({
    titulo = "Oferta Pendiente",
    subtitulo = "Notificación cuando acepten",
    tiempoEspera = "00:00:37 ms",
    estadoTexto = "Pendiente",
}) => {
    return (
        <VStack
            gap={4}
            align="center"
            justify="center"
            p={8}
            border="1px solid"
            borderColor="gray.200"
            borderRadius="lg"
            bg="white"
            minH="200px"
        >
            <Box color="yellow.500">
                <LuClock size={48} />
            </Box>
            <Text fontSize="xl" fontWeight="bold" textAlign="center">
                {titulo}
            </Text>
            <Text fontSize="sm" color="gray.500" textAlign="center">
                Status: {estadoTexto}
            </Text>
            <Text fontSize="lg" fontWeight="mono" color="gray.700">
                {tiempoEspera}
            </Text>
            <Text fontSize="xs" color="gray.400" textAlign="center">
                {subtitulo}
            </Text>
        </VStack>
    );
};

export default PanelEstadoOferta;
