import { VStack, Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelResultadoFinal from "../componentes/organismos/PanelResultadoFinal";
import Boton from "../componentes/atomos/Boton";

const PaginaResultadoFinalComprador = ({ rol = "recolector" }) => {
    const navigate = useNavigate();

    if (rol === "centro") {
        return (
            <DiseniodeAplicacion titulo="Operación completada" mostrarAtras={true}>
                <VStack gap={4} align="stretch">
                    <Text fontSize="xl" fontWeight="bold" textAlign="center">
                        Operación Completada
                    </Text>
                    <Text fontSize="sm" textAlign="center" color="gray.600">
                        Mat. Recibido: Cartón 250kg
                    </Text>
                    <Text fontSize="sm" textAlign="center" color="gray.600">
                        Pago Total: $25.00
                    </Text>
                    <Boton
                        texto="Finalizar"
                        variante="solid"
                        colorEsquema="gray"
                        ancho="full"
                        alHacer={() => navigate("/seleccionar-rol")}
                    />
                </VStack>
            </DiseniodeAplicacion>
        );
    }

    return (
        <DiseñodeAplicacion titulo="Resultado final" mostrarAtras={true}>
            <PanelResultadoFinal
                transaccionId="TX-2026-001"
                alVolver={() => navigate("/recolector/inicio")}
            />
        </DiseñodeAplicacion>
    );
};

export default PaginaResultadoFinalComprador;
