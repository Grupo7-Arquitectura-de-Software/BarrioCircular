import { VStack, Box, Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import EtiquetaInformacion from "../componentes/moleculas/EtiquetaInformacion";
import Boton from "../componentes/atomos/Boton";


const PaginadeConfirmacionOperacion = () => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
            <VStack gap={4} align="stretch" w="100%">
                <Text fontSize="md" fontWeight="bold">Summario de verificación</Text>

                <Box p={3} border="1px solid" borderColor="gray.200" borderRadius="md">
                    <VStack gap={1} align="stretch">
                        <EtiquetaInformacion etiqueta="Tipo de material:" valor="Plástico PET" />
                        <EtiquetaInformacion etiqueta="Peso estimado:" valor="15kg" />
                        <EtiquetaInformacion etiqueta="Precio ref.:" valor="$2.50/kg" />
                        <EtiquetaInformacion etiqueta="Observaciones:" valor="Distancia problema" />
                    </VStack>
                </Box>

                <Boton
                    texto="Confirmar operación"
                    variante="solid"
                    colorEsquema="gray"
                    ancho="full"
                    alHacer={() => navigate("/recolector/resultado")}
                />
            </VStack>
        </DiseniodeAplicacion>
    );
};

export default PaginadeConfirmacionOperacion;