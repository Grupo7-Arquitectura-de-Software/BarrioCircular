import { Box, VStack, HStack, Text } from "@chakra-ui/react";
import { LuBox, LuPillBottle, LuLeaf } from "react-icons/lu";
import IndicadorPuntuacion from "../atomos/IndicadorPuntuacion";

const ICONOS_MATERIAL = {
  carton: <LuBox />,
  plastico: <LuPillBottle />,
  papel: <LuLeaf />,
  metal: <LuBox />,
  vidrio: <LuPillBottle />,
};

const TarjetaPublicacionRecomendada = ({
  tipoMaterial = "Plástico PET",
  iconoMaterial = "plastico",
  pesoEstimado = 15,
  precioReferencial = "$2.50/kg",
  distancia = "3.2 km",
  puntuacion = 9.2,
  alVerDetalle,
}) => {
  return (
    <Box
      border="1px solid"
      borderColor="gray.300"
      borderRadius="md"
      p={3}
      cursor="pointer"
      _hover={{ bg: "gray.50", borderColor: "gray.500" }}
      onClick={alVerDetalle}
      bg="white"
    >
      <HStack gap={3} align="flex-start">
        <Box
          w="40px"
          h="40px"
          bg="gray.100"
          borderRadius="md"
          display="flex"
          alignItems="center"
          justifyContent="center"
          flexShrink={0}
          fontSize="xl"
          color="gray.600"
        >
          {ICONOS_MATERIAL[iconoMaterial] || <LuBox />}
        </Box>
        <VStack align="flex-start" gap={0.5} flex={1}>
          <Text fontSize="sm" fontWeight="bold">
            {tipoMaterial}
          </Text>
          <Text fontSize="xs" color="gray.600">
            Tipo de material: {tipoMaterial}
          </Text>
          <Text fontSize="xs" color="gray.600">
            Peso estimado: {pesoEstimado}kg
          </Text>
          <Text fontSize="xs" color="gray.600">
            Precio referencial: {precioReferencial}
          </Text>
          <Text fontSize="xs" color="gray.600">
            Distancia aproximada: {distancia}
          </Text>
          <IndicadorPuntuacion puntuacion={puntuacion} />
        </VStack>
      </HStack>
    </Box>
  );
};

export default TarjetaPublicacionRecomendada;
