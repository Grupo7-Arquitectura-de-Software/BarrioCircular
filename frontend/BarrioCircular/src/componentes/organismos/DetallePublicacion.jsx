import { VStack, Box, Text, HStack } from "@chakra-ui/react";
import { LuMapPin, LuUser } from "react-icons/lu";
import EtiquetaInformacion from "../moleculas/EtiquetaInformacion";
import Boton from "../atomos/Boton";

/**
 * Organismo: Detalle completo de una publicación para el flujo Recolector/Centro
 * @param {string} tipoMaterial - Tipo de material
 * @param {string} descripcion - Descripción del material
 * @param {number} pesoKg - Peso estimado en kg
 * @param {string} precio - Precio referencial
 * @param {string} vendedor - Nombre del vendedor
 * @param {string} ubicacion - Ubicación de recogida
 * @param {function} alRealizarOferta - Callback para ir a realizar oferta
 */
const DetallePublicacion = ({
    tipoMaterial = "Plástico PET",
    descripcion = "Material foto garantizado, plástico válido, detalle continuo sin errores, descripción, cuéllenme maíz/carne.",
    pesoKg = 15,
    precio = "$2.50/kg",
    vendedor = "Juan P. (Reciclador)",
    ubicacion = "Av. América, Quito",
    alRealizarOferta,
}) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            {/* Foto del material */}
            <Box
                w="100%"
                h="180px"
                bg="gray.300"
                borderRadius="md"
                display="flex"
                alignItems="center"
                justifyContent="center"
                color="gray.500"
                border="1px solid"
                borderColor="gray.400"
            >
                <Text fontSize="sm">Material photo</Text>
            </Box>

            {/* Descripción */}
            <Box>
                <Text fontSize="sm" color="gray.600" lineHeight="1.5">
                    {descripcion}
                </Text>
            </Box>

            {/* Mapa placeholder */}
            <Box
                w="100%"
                h="120px"
                bg="gray.200"
                borderRadius="md"
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                color="gray.400"
                border="1px dashed"
                borderColor="gray.400"
                gap={1}
            >
                <LuMapPin size={24} />
                <Text fontSize="xs">Locación en el mapa</Text>
            </Box>

            {/* Datos del vendedor */}
            <HStack gap={3} align="center" p={3} border="1px solid" borderColor="gray.200" borderRadius="md">
                <Box
                    w="36px"
                    h="36px"
                    borderRadius="full"
                    bg="gray.200"
                    display="flex"
                    alignItems="center"
                    justifyContent="center"
                >
                    <LuUser size={18} color="gray" />
                </Box>
                <VStack align="flex-start" gap={0}>
                    <Text fontSize="sm" fontWeight="semibold">{vendedor}</Text>
                    <Text fontSize="xs" color="gray.500">Peso: {pesoKg}kg</Text>
                </VStack>
            </HStack>

            {/* Información clave */}
            <VStack gap={1} align="stretch">
                <EtiquetaInformacion etiqueta="Vendedor:" valor={vendedor} />
                <EtiquetaInformacion etiqueta="Tipo Material:" valor={tipoMaterial} />
                <EtiquetaInformacion etiqueta="Precio Ref.:" valor={precio} />
                <EtiquetaInformacion etiqueta="Ubicación:" valor={ubicacion} />
            </VStack>

            {alRealizarOferta && (
                <Boton
                    texto="Realizar Oferta"
                    variante="solid"
                    colorEsquema="gray"
                    ancho="full"
                    alHacer={alRealizarOferta}
                />
            )}
        </VStack>
    );
};

export default DetallePublicacion;
