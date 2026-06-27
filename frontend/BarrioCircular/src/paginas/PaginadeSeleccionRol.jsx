import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { VStack, Text, Box } from "@chakra-ui/react";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import SelectordeRol from "../componentes/organismos/SelectordeRol";
import LogotipoApp from "../componentes/atomos/LogotipoApp";
import Boton from "../componentes/atomos/Boton";

const PaginadeSeleccionRol = () => {
    const navigate = useNavigate();
    const [mostrarRoles, setMostrarRoles] = useState(false);

    return (
        <DiseniodeAutenticacion>
            {!mostrarRoles ? (
                <VStack 
                    gap={8} 
                    align="center" 
                    justify="center" 
                    minH="350px"
                    animation="fadeInUp 0.6s ease-out"
                    sx={{
                        "@keyframes fadeInUp": {
                            "0%": { opacity: 0, transform: "translateY(30px)" },
                            "100%": { opacity: 1, transform: "translateY(0)" }
                        }
                    }}
                >
                    <Box 
                        transform="scale(1.2)" 
                        transition="transform 0.3s"
                        _hover={{ transform: "scale(1.25)" }}
                    >
                        <LogotipoApp tamanio="lg" />
                    </Box>
                    <VStack gap={3}>
                        <Text fontSize="2xl" fontWeight="bold" textAlign="center" color="gray.800">
                            Bienvenido a BarrioCircular
                        </Text>
                        <Text fontSize="md" color="gray.600" textAlign="center" lineHeight="1.6" maxW="90%">
                            Conecta ciudadanos, recicladores y centros de recolección para un futuro más sostenible.
                        </Text>
                    </VStack>
                    <Box w="100%" mt={4}>
                        <Boton 
                            texto="Comenzar" 
                            variante="solid" 
                            colorEsquema="blue" 
                            ancho="full" 
                            tamanio="lg"
                            alHacer={() => setMostrarRoles(true)}
                        />
                    </Box>
                </VStack>
            ) : (
                <Box
                    animation="slideIn 0.5s cubic-bezier(0.4, 0, 0.2, 1)"
                    sx={{
                        "@keyframes slideIn": {
                            "0%": { opacity: 0, transform: "translateX(30px)" },
                            "100%": { opacity: 1, transform: "translateX(0)" }
                        }
                    }}
                >
                    <SelectordeRol
                        alSeleccionarCiudadano={() => navigate("/ciudadano/crear-publicacion")}
                        alSeleccionarRecolector={() => navigate("/recolector/inicio")}
                        alSeleccionarCentro={() => navigate("/centro/buscar-materiales")}
                    />
                </Box>
            )}
        </DiseniodeAutenticacion>
    );
};

export default PaginadeSeleccionRol;
