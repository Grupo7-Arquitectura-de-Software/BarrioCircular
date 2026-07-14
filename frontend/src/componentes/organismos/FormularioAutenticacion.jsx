import { useState } from "react";
import { VStack, HStack, Text, Box, Circle, Flex } from "@chakra-ui/react";
import { LuArrowLeft } from "react-icons/lu";
import LogotipoApp from "../atomos/LogotipoApp";
import CampoEntrada from "../atomos/CampoEntrada";
import Boton from "../atomos/Boton";
import EtiquetadeEstado from "../atomos/EtiquetadeEstado";

const IndicadorProgreso = ({ paso, totalPasos }) => (
  <HStack gap={3} justify="center" my={2}>
    {[1, 2].map((i) => (
      <HStack key={i} gap={3}>
        <Circle size="10px" bg={paso >= i ? "blue.500" : "gray.200"} transition="all 0.3s" />
        {i < totalPasos && (
          <Box
            w="30px"
            h="3px"
            borderRadius="full"
            bg={paso > i ? "blue.500" : "gray.200"}
            transition="all 0.3s"
          />
        )}
      </HStack>
    ))}
  </HStack>
);

const FormularioAutenticacion = ({ etiquetaRol, alContinuar, alVolver }) => {
  const [modo, setModo] = useState("login");
  const [paso, setPaso] = useState(1);
  const esLogin = modo === "login";
  const totalPasos = 2;

  const cambiarModo = (nuevoModo) => {
    setModo(nuevoModo);
    setPaso(1);
  };

  const avanzarPaso = () => {
    if (paso < totalPasos) setPaso(paso + 1);
    else alContinuar();
  };

  const retrocederPaso = () => {
    if (paso > 1) setPaso(paso - 1);
    else alVolver();
  };

  return (
    <VStack gap={5} align="stretch" w="100%">
      <Flex justify="space-between" align="center">
        <Box
          cursor="pointer"
          color="gray.400"
          _hover={{ color: "gray.800", transform: "translateX(-2px)" }}
          onClick={retrocederPaso}
          transition="all 0.2s"
          p={1}
        >
          <LuArrowLeft size={24} />
        </Box>
        <EtiquetadeEstado color="blue" texto={etiquetaRol} />
        <Box w="32px" /> {/* Spacer para centrar la etiqueta */}
      </Flex>

      <VStack gap={3} align="center">
        <LogotipoApp tamanio="sm" />
        <Text fontSize="xl" fontWeight="bold" textAlign="center" color="gray.800">
          {esLogin ? "Bienvenido de nuevo" : "Crea tu cuenta"}
        </Text>
      </VStack>

      {paso === 1 && (
        <VStack gap={2} w="100%" bg="gray.100" p={1.5} borderRadius="xl">
          <Boton
            texto="Ingresar"
            variante={esLogin ? "solid" : "ghost"}
            colorEsquema={esLogin ? "blue" : "gray"}
            ancho="full"
            tamanio="sm"
            alHacer={() => cambiarModo("login")}
          />
          <Boton
            texto="Registrarse"
            variante={!esLogin ? "solid" : "ghost"}
            colorEsquema={!esLogin ? "blue" : "gray"}
            ancho="full"
            tamanio="sm"
            alHacer={() => cambiarModo("registro")}
          />
        </VStack>
      )}

      <IndicadorProgreso paso={paso} totalPasos={totalPasos} />

      <Box
        animation="fadeInSlide 0.4s cubic-bezier(0.4, 0, 0.2, 1)"
        key={paso}
        sx={{
          "@keyframes fadeInSlide": {
            "0%": { opacity: 0, transform: "translateX(15px)" },
            "100%": { opacity: 1, transform: "translateX(0)" },
          },
        }}
      >
        <VStack gap={5} align="stretch">
          {paso === 1 ? (
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={2} color="gray.700">
                Correo electrónico
              </Text>
              <CampoEntrada marcadordePosicion="ejemplo@correo.com" tipo="email" tamanio="lg" />
            </Box>
          ) : (
            <VStack gap={5} align="stretch">
              <Box>
                <Text fontSize="sm" fontWeight="medium" mb={2} color="gray.700">
                  Contraseña
                </Text>
                <CampoEntrada marcadordePosicion="••••••••" tipo="password" tamanio="lg" />
              </Box>
              {!esLogin && (
                <Box>
                  <Text fontSize="sm" fontWeight="medium" mb={2} color="gray.700">
                    Confirma tu contraseña
                  </Text>
                  <CampoEntrada marcadordePosicion="••••••••" tipo="password" tamanio="lg" />
                </Box>
              )}
            </VStack>
          )}
        </VStack>
      </Box>

      <Box mt={2}>
        <Boton
          texto={paso === 1 ? "Siguiente" : esLogin ? "Entrar a mi cuenta" : "Crear mi cuenta"}
          variante="solid"
          colorEsquema="blue"
          ancho="full"
          tamanio="lg"
          alHacer={avanzarPaso}
        />
      </Box>
    </VStack>
  );
};

export default FormularioAutenticacion;
