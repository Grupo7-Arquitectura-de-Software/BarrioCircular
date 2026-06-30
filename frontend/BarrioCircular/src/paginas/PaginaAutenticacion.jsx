import { SignIn } from "@clerk/clerk-react";
import { Box, VStack } from "@chakra-ui/react";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import LogotipoApp from "../componentes/atomos/LogotipoApp";

const PaginaAutenticacion = () => {
    return (
        <DiseniodeAutenticacion>
            <VStack gap={6} align="center" justify="center">
                <Box transform="scale(1.2)" mb={4}>
                    <LogotipoApp tamanio="lg" />
                </Box>
                <SignIn routing="path" path="/auth" />
            </VStack>
        </DiseniodeAutenticacion>
    );
};

export default PaginaAutenticacion;
