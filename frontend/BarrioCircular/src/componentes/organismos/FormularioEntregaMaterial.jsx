import { VStack, Box, Text } from "@chakra-ui/react";
import { LuCamera } from "react-icons/lu";
import ItemVerificacion from "../moleculas/ItemVerificacion";
import Boton from "../atomos/Boton";

const ITEMS_ENTREGA = [
    "Confirmar entrega física",
    "Material en buen estado",
    "Peso verificado",
];

/**
 * Organismo: Formulario de entrega del material con checklist y foto
 * @param {function} alConfirmar - Callback al confirmar entrega
 */
const FormularioEntregaMaterial = ({ alConfirmar }) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            <Text fontSize="sm" fontWeight="medium" color="gray.700">
                Confirme entregar física modo:
            </Text>

            {/* Checklist */}
            <VStack gap={2} align="stretch">
                {ITEMS_ENTREGA.map((item) => (
                    <ItemVerificacion key={item} etiqueta={item} />
                ))}
                <ItemVerificacion etiqueta="Confirmar entregar" />
            </VStack>

            {/* Área de foto */}
            <Box
                w="100%"
                h="140px"
                bg="gray.100"
                borderRadius="md"
                border="1px dashed"
                borderColor="gray.400"
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                gap={2}
                cursor="pointer"
                _hover={{ bg: "gray.200" }}
                color="gray.400"
            >
                <LuCamera size={32} />
                <Text fontSize="xs">Tomar foto de confirmación</Text>
            </Box>

            <Boton
                texto="Confirm Delivery"
                variante="solid"
                colorEsquema="gray"
                ancho="full"
                alHacer={alConfirmar}
            />
        </VStack>
    );
};

export default FormularioEntregaMaterial;
