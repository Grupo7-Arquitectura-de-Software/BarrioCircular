import { VStack, HStack, Box, Text } from "@chakra-ui/react";
import { LuCircleCheck } from "react-icons/lu";
import EtiquetadeEstado from "../atomos/EtiquetadeEstado";
import EtiquetaInformacion from "../moleculas/EtiquetaInformacion";
import Divisor from "../atomos/Divisor";
import Boton from "../atomos/Boton";

const PanelResultadoFinal = ({
    tipoMaterial = "Cartón",
    pesoKg = 10,
    distancia = "2km",
    observacion = "$5.00",
    recolector = "Reciclador Juan",
    transaccionId = null,
    estadosBadge = [
        { color: "green", texto: "Completada" },
        { color: "yellow", texto: "Completada con ajuste" },
        { color: "red", texto: "Cancelada" },
        { color: "blue", texto: "En revisión" },
    ],
    alVolver,
}) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            {transaccionId ? (
                /* Vista tipo "Transacción Completada" (flujo recolector) */
                <VStack gap={3} align="center" py={4}>
                    <Box color="green.500">
                        <LuCircleCheck size={52} />
                    </Box>
                    <Text fontSize="xl" fontWeight="bold" textAlign="center">
                        ¡Transacción Completada!
                    </Text>
                    <Text fontSize="sm" color="gray.500">
                        Transacción ID: {transaccionId}
                    </Text>
                    <Boton texto="Volver a Ofertas" variante="solid" colorEsquema="gray" alHacer={alVolver} />
                </VStack>
            ) : (
                <>

                    <Box
                        w="80px"
                        h="80px"
                        bg="gray.200"
                        borderRadius="md"
                        display="flex"
                        alignItems="center"
                        justifyContent="center"
                        mx="auto"
                    >
                        <Text fontSize="xs" color="gray.400">📦</Text>
                    </Box>

                    <VStack gap={1} align="stretch">
                        <EtiquetaInformacion etiqueta="Material:" valor={tipoMaterial} />
                        <EtiquetaInformacion etiqueta="Peso:" valor={`${pesoKg}kg`} />
                        <EtiquetaInformacion etiqueta="Distancia:" valor={distancia} />
                        <EtiquetaInformacion etiqueta="Observación:" valor={observacion} />
                    </VStack>

                    <HStack gap={1} align="center">
                        <Box
                            w="36px" h="36px" borderRadius="full" bg="gray.200"
                            display="flex" alignItems="center" justifyContent="center"
                        />
                        <Text fontSize="sm">{recolector}</Text>
                    </HStack>

                    <Divisor />

                    <Box>
                        <Text fontSize="sm" fontWeight="semibold" mb={2}>
                            Status:
                        </Text>
                        <HStack gap={2} flexWrap="wrap">
                            {estadosBadge.map((b) => (
                                <EtiquetadeEstado key={b.texto} color={b.color} texto={b.texto} />
                            ))}
                        </HStack>
                    </Box>

                    <Box>
                        <Text fontSize="xs" color="gray.500" fontWeight="medium" mb={1}>
                            Outcome:
                        </Text>
                        <VStack align="flex-start" gap={0.5}>
                            <Text fontSize="xs" color="gray.600">• Completada con ajuste</Text>
                            <Text fontSize="xs" color="gray.600">• Completada con acelerada</Text>
                            <Text fontSize="xs" color="gray.600">• En revisión</Text>
                        </VStack>
                    </Box>
                </>
            )}
        </VStack>
    );
};

export default PanelResultadoFinal;
