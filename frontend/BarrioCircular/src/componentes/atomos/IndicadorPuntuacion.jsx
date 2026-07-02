import { HStack, Text, Box } from "@chakra-ui/react";
import { LuStar } from "react-icons/lu";

/**
 * Átomo: Indicador de puntuación para Match Inteligente
 * @param {number} puntuacion - Valor numérico (ej. 9.2)
 * @param {number} maximo - Valor máximo (ej. 10)
 * @param {string} etiqueta - Texto descriptivo (ej. "Match Inteligente")
 */
const IndicadorPuntuacion = ({ puntuacion = 9.2, maximo = 10, etiqueta = "Match Inteligente" }) => {
  return (
    <HStack gap={1} align="center">
      <Box color="yellow.500">
        <LuStar size={14} fill="currentColor" />
      </Box>
      <Text fontSize="xs" fontWeight="semibold" color="gray.700">
        {puntuacion}/{maximo}
      </Text>
      <Text fontSize="xs" color="gray.500">
        {etiqueta}
      </Text>
    </HStack>
  );
};

export default IndicadorPuntuacion;
