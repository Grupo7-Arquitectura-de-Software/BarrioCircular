import { Box, VStack, Text } from "@chakra-ui/react";
import AvatarUsuario from "../atomos/AvatarUsuario";
import EtiquetaInformacion from "../moleculas/EtiquetaInformacion";
import Divisor from "../atomos/Divisor";
import Boton from "../atomos/Boton";

const ModalOfertaAceptada = ({
    nombreOfertante = "Reciclador Juan",
    tipoOfertante = "Reciclador",
    monto = "$4.80",
    distancia = "2km",
    observacion = "Recogida rápida",
    esSeleccionada = false,
    alAceptar,
    alRechazar,
}) => {
    return (
        <Box
            p={3}
            border="1px solid"
            borderColor={esSeleccionada ? "gray.700" : "gray.200"}
            borderRadius="md"
            bg={esSeleccionada ? "gray.50" : "white"}
            w="100%"
        >
            <VStack gap={2} align="stretch">
                <AvatarUsuario nombre={nombreOfertante} tipo={tipoOfertante} tamanio="sm" />
                <Divisor margen="1" />
                <EtiquetaInformacion etiqueta="Monto:" valor={monto} />
                <EtiquetaInformacion etiqueta="Distancia:" valor={distancia} />
                <EtiquetaInformacion etiqueta="Observación:" valor={observacion} />

                {esSeleccionada && (
                    <Text fontSize="xs" color="gray.400" fontStyle="italic">
                        En revisión
                    </Text>
                )}

                <VStack gap={2} mt={1}>
                    <Boton
                        texto="Aceptar"
                        variante="solid"
                        colorEsquema="gray"
                        ancho="full"
                        tamanio="sm"
                        alHacer={alAceptar}
                    />
                    <Boton
                        texto="Rechazar"
                        variante="outline"
                        colorEsquema="red"
                        ancho="full"
                        tamanio="sm"
                        alHacer={alRechazar}
                    />
                </VStack>
            </VStack>
        </Box>
    );
};

export default ModalOfertaAceptada;
