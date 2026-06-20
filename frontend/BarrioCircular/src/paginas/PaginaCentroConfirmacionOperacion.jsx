import { VStack, Box, Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion.jsx";
import Boton from "@/componentes/atomos/Boton";

const PaginaCentroConfirmacionOperacion = () => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="Confirmar operación" mostrarAtras={true}>
            <VStack gap={4} align="stretch" w="100%">
                <Text fontSize="md" fontWeight="bold">Summario de verificación</Text>

                <Box p={3} border="1px solid" borderColor="gray.200" borderRadius="md">
                    <VStack gap={1} align="stretch">
                        <EtiquetaInformacion etiqueta="Tipo de material:" valor="Cartón" />
                        <EtiquetaInformacion etiqueta="Peso estimado:" valor="250kg" />
                        <EtiquetaInformacion etiqueta="Precio ref.:" valor="$0.10/kg" />
                        <EtiquetaInformacion etiqueta="Observaciones:" valor="Material en buen estado" />
                    </VStack>
                </Box>

                <Boton
                    texto="Confirmar operación"
                    variante="solid"
                    colorEsquema="gray"
                    ancho="full"
                    alHacer={() => navigate("/centro/resultado")}
                />
            </VStack>
        </DiseniodeAplicacion>
    );
};

export default PaginaCentroConfirmacionOperacion;
