import { VStack, Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodePanelControl from "../componentes/plantillas/DiseniodePanelControl.jsx";
import ChatdeCoordinacion from "../componentes/organismos/ChatdeCoordinacion";
import Boton from "../componentes/atomos/Boton";


const PaginadeColeccionCoordenadas = ({ prefijoRuta = "/ciudadano" }) => {
    const navigate = useNavigate();

    return (
        <DiseniodePanelControl titulo="BarrioCircular" mostrarAtras={true}>
            <VStack gap={4} align="stretch" h="100%">
                <Text fontSize="xs" color="gray.500" textAlign="center">
                    Coordinar Recolección
                </Text>
                <ChatdeCoordinacion participante="Reciclador Juan" />
                <Boton
                    texto="Entregar Material"
                    variante="solid"
                    colorEsquema="gray"
                    ancho="full"
                    alHacer={() => navigate(`${prefijoRuta}/entregar-material`)}
                />
            </VStack>
        </DiseniodePanelControl>
    );
};

export default PaginadeColeccionCoordenadas;