import { VStack, Text, SimpleGrid, Box } from "@chakra-ui/react";
import { LuShoppingBag, LuPackagePlus } from "react-icons/lu";
import Boton from "../atomos/Boton";

/**
 * Organismo: Panel inicial del reciclador con acceso a vender y comprar materiales.
 */
const PanelInicioRecolector = ({ alVender, alComprar }) => {
  return (
    <VStack gap={6} align="stretch" w="100%">
      <VStack gap={1} align="center">
        <Text fontSize={{ base: "lg", md: "xl" }} fontWeight="bold" textAlign="center">
          ¿Qué deseas hacer hoy?
        </Text>
        <Text fontSize="sm" color="gray.600" textAlign="center">
          Como reciclador puedes vender tus propios materiales o comprar de otros vendedores.
        </Text>
      </VStack>

      <SimpleGrid columns={{ base: 1, md: 2 }} gap={4}>
        <Box p={5} border="1px solid" borderColor="gray.200" borderRadius="lg" bg="gray.50">
          <VStack gap={3} align="stretch">
            <Box color="gray.700" alignSelf="center">
              <LuPackagePlus size={36} />
            </Box>
            <Text fontSize="md" fontWeight="semibold" textAlign="center">
              Vender material
            </Text>
            <Text fontSize="sm" color="gray.600" textAlign="center">
              Publica materiales reciclables para que los compradores los reserven.
            </Text>
            <Boton
              texto="Ir a vender"
              variante="solid"
              colorEsquema="gray"
              ancho="full"
              alHacer={alVender}
            />
          </VStack>
        </Box>

        <Box p={5} border="1px solid" borderColor="gray.200" borderRadius="lg" bg="white">
          <VStack gap={3} align="stretch">
            <Box color="gray.700" alignSelf="center">
              <LuShoppingBag size={36} />
            </Box>
            <Text fontSize="md" fontWeight="semibold" textAlign="center">
              Comprar material
            </Text>
            <Text fontSize="sm" color="gray.600" textAlign="center">
              Explora publicaciones recomendadas y reserva materiales.
            </Text>
            <Boton
              texto="Ir a comprar"
              variante="outline"
              colorEsquema="gray"
              ancho="full"
              alHacer={alComprar}
            />
          </VStack>
        </Box>
      </SimpleGrid>
    </VStack>
  );
};

export default PanelInicioRecolector;
