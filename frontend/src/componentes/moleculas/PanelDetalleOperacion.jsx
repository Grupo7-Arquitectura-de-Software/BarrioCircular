import { Badge, Box, Flex, Text, VStack } from "@chakra-ui/react";
import { MdOutlineImage } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

/**
 * Panel lateral "Detalle de Operación" (mockups Entregable 4): imagen con
 * insignia de estado y filas de material, peso/ID y monto acordado.
 */
const PanelDetalleOperacion = ({
  titulo = "Detalle de Operación",
  insignia,
  material,
  filas = [],
  monto,
  etiquetaMonto = "Monto Acordado",
}) => {
  return (
    <Box
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
    >
      <Box position="relative" h="120px" bg="fondo.pagina">
        <Flex h="100%" align="center" justify="center" color="gray.300">
          <Icono componente={<MdOutlineImage />} tamanio="3xl" color="gray.300" />
        </Flex>
        {insignia && (
          <Badge
            position="absolute"
            top={3}
            left={3}
            bg="verde.100"
            color="marca.primario"
            borderRadius="md"
            px={2}
            py={1}
          >
            {insignia}
          </Badge>
        )}
      </Box>
      <VStack align="stretch" gap={0} p={5}>
        <Text fontFamily="heading" fontWeight="700" fontSize="lg" mb={3}>
          {titulo}
        </Text>
        <Flex justify="space-between" py={2} borderBottom="1px solid" borderColor="gray.100">
          <Text color="gray.600" fontSize="sm">
            Material
          </Text>
          <Text fontWeight="600" fontSize="sm">
            {material}
          </Text>
        </Flex>
        {filas.map(({ etiqueta, valor, esInsignia }) => (
          <Flex
            key={etiqueta}
            justify="space-between"
            align="center"
            py={2}
            borderBottom="1px solid"
            borderColor="gray.100"
          >
            <Text color="gray.600" fontSize="sm">
              {etiqueta}
            </Text>
            {esInsignia ? (
              <Badge bg="fondo.cabeceraTarjeta" borderRadius="md" px={2}>
                {valor}
              </Badge>
            ) : (
              <Text fontWeight="600" fontSize="sm">
                {valor}
              </Text>
            )}
          </Flex>
        ))}
        {monto && (
          <Flex justify="space-between" align="center" py={2}>
            <Text color="gray.600" fontSize="sm">
              {etiquetaMonto}
            </Text>
            <Text fontFamily="heading" fontWeight="700" color="marca.primario">
              {monto}
            </Text>
          </Flex>
        )}
      </VStack>
    </Box>
  );
};

export default PanelDetalleOperacion;
