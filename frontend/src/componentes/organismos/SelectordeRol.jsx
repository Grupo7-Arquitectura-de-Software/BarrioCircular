import { VStack, Box, Text, HStack } from "@chakra-ui/react";
import { LuUser, LuRecycle, LuBuilding2 } from "react-icons/lu";

const TARJETAS_ROL = [
  {
    id: "ciudadano",
    titulo: "Ciudadano",
    descripcion: "Publica materiales y gestiona ofertas.",
    icono: LuUser,
  },
  {
    id: "recolector",
    titulo: "Reciclador",
    descripcion: "Compra o recolecta materiales.",
    icono: LuRecycle,
  },
  {
    id: "centro",
    titulo: "Centro de Recolección",
    descripcion: "Recibe materiales a gran escala.",
    icono: LuBuilding2,
  },
];

/**
 * Organismo: Selector de rol fluido
 */
const SelectordeRol = ({
  alSeleccionarCiudadano,
  alSeleccionarRecolector,
  alSeleccionarCentro,
}) => {
  const callbacks = {
    ciudadano: alSeleccionarCiudadano,
    recolector: alSeleccionarRecolector,
    centro: alSeleccionarCentro,
  };

  return (
    <VStack gap={6} align="stretch" w="100%">
      <VStack gap={1} align="center" mb={2}>
        <Text fontSize="xl" fontWeight="bold" textAlign="center" color="gray.800">
          Selecciona tu perfil
        </Text>
        <Text fontSize="sm" color="gray.500" textAlign="center">
          Personalizaremos tu experiencia según tu rol
        </Text>
      </VStack>

      <VStack gap={4}>
        {TARJETAS_ROL.map(({ id, titulo, descripcion, icono: Icono }) => (
          <Box
            key={id}
            p={4}
            w="100%"
            border="2px solid"
            borderColor="transparent"
            borderRadius="xl"
            bg="gray.50"
            cursor="pointer"
            transition="all 0.3s cubic-bezier(0.4, 0, 0.2, 1)"
            _hover={{
              bg: "white",
              borderColor: "blue.400",
              shadow: "md",
              transform: "translateY(-3px)",
            }}
            onClick={callbacks[id]}
          >
            <HStack gap={4}>
              <Box p={3} borderRadius="lg" bg="white" color="blue.500" shadow="sm">
                <Icono size={24} />
              </Box>
              <VStack align="start" gap={0} flex={1}>
                <Text fontSize="md" fontWeight="bold" color="gray.800">
                  {titulo}
                </Text>
                <Text fontSize="sm" color="gray.600">
                  {descripcion}
                </Text>
              </VStack>
            </HStack>
          </Box>
        ))}
      </VStack>
    </VStack>
  );
};

export default SelectordeRol;
