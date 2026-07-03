import { Box, Circle, HStack, Text } from "@chakra-ui/react";
import { MdStar } from "react-icons/md";

const obtenerIniciales = (nombre = "") =>
  nombre
    .split(" ")
    .map((palabra) => palabra[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase();

/**
 * Tarjeta compacta del vendedor (mockups Entregable 4): avatar con
 * iniciales, rótulo del tipo de vendedor, nombre y calificación.
 */
const TarjetaVendedor = ({
  rotulo = "VENDEDOR",
  nombre = "Vendedor",
  calificacion = "4.9",
  detalleCalificacion = "",
}) => {
  return (
    <HStack
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      p={4}
      gap={3}
    >
      <Circle size="48px" bg="marca.secundario" color="white" fontWeight="700">
        {obtenerIniciales(nombre)}
      </Circle>
      <Box>
        <Text fontSize="xs" color="gray.500" fontWeight="600" letterSpacing="wide">
          {rotulo}
        </Text>
        <Text fontFamily="heading" fontWeight="600">
          {nombre}
        </Text>
        <HStack gap={1} fontSize="sm" color="gray.600">
          <MdStar color="var(--chakra-colors-yellow-500)" />
          <Text>
            {calificacion} {detalleCalificacion}
          </Text>
        </HStack>
      </Box>
    </HStack>
  );
};

export default TarjetaVendedor;
