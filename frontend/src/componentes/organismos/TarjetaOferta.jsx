import {
  Badge,
  Box,
  Button,
  Circle,
  Flex,
  HStack,
  SimpleGrid,
  Square,
  Text,
  VStack,
} from "@chakra-ui/react";
import { MdCheckCircleOutline, MdOutlineInventory2, MdOutlinePerson, MdStar } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

/**
 * Tarjeta de oferta recibida (mockups "Ofertas Recibidas" / "Aceptar Oferta"):
 * tarjeta verde con ofertante, calificación, monto, distancia, observación,
 * material y acciones Aceptar/Rechazar.
 */
const TarjetaOferta = ({
  nombreOfertante = "Reciclador Juan",
  tipoOfertante = "Reciclador",
  calificacion = "4.8",
  monto = "$4.80",
  distancia = "2km",
  observacion = "Recogida rápida",
  materialTitulo = "Cartón corrugado limpio",
  materialDetalle = "10kg • Publicado hace 1h",
  alAceptar,
  alRechazar,
}) => {
  return (
    <Box bg="verde.50" border="1px solid" borderColor="verde.200" borderRadius="xl" p={5}>
      {/* Ofertante */}
      <Flex
        justify="space-between"
        align="center"
        pb={4}
        borderBottom="1px solid"
        borderColor="verde.200"
      >
        <HStack gap={3}>
          <Circle size="44px" bg="fondo.tarjeta" color="marca.primario">
            <Icono componente={<MdOutlinePerson />} tamanio="xl" color="marca.primario" />
          </Circle>
          <Box>
            <Text fontFamily="heading" fontWeight="600" fontSize="lg" color="marca.primario">
              {nombreOfertante}
            </Text>
            <Text fontSize="sm" color="verde.500">
              (Tipo: {tipoOfertante})
            </Text>
          </Box>
        </HStack>
        <Badge bg="fondo.tarjeta" color="gray.800" borderRadius="full" px={2} py={1} boxShadow="sm">
          <Icono componente={<MdStar />} tamanio="sm" color="yellow.500" /> {calificacion}
        </Badge>
      </Flex>

      {/* Monto y distancia */}
      <VStack align="stretch" gap={2} py={4}>
        <Flex justify="space-between" align="center">
          <Text color="gray.600">Monto:</Text>
          <Text fontFamily="heading" fontWeight="700" fontSize="2xl" color="marca.primario">
            {monto}
          </Text>
        </Flex>
        <Flex justify="space-between" align="center">
          <Text color="gray.600">Distancia:</Text>
          <Text fontWeight="600">{distancia}</Text>
        </Flex>
        <Flex bg="verde.100" borderRadius="lg" px={3} py={2} justify="space-between" gap={3}>
          <Text fontSize="sm" color="gray.600" flexShrink={0}>
            Observación:
          </Text>
          <Text fontSize="sm" textAlign="right">
            {observacion}
          </Text>
        </Flex>
      </VStack>

      {/* Material ofertado */}
      <HStack
        bg="fondo.tarjeta"
        border="1px solid"
        borderColor="verde.200"
        borderRadius="lg"
        p={3}
        gap={3}
        mb={4}
      >
        <Square size="40px" bg="fondo.cabeceraTarjeta" borderRadius="md">
          <Icono componente={<MdOutlineInventory2 />} tamanio="lg" color="marca.secundario" />
        </Square>
        <Box>
          <Text fontWeight="600" fontSize="sm">
            {materialTitulo}
          </Text>
          <Text fontSize="xs" color="gray.600">
            {materialDetalle}
          </Text>
        </Box>
      </HStack>

      {/* Acciones */}
      <SimpleGrid columns={2} gap={3}>
        <Button colorPalette="verde" bg="marca.primario" rounded="lg" onClick={alAceptar}>
          <MdCheckCircleOutline /> Aceptar Oferta
        </Button>
        <Button
          variant="outline"
          colorPalette="gray"
          bg="fondo.tarjeta"
          rounded="lg"
          onClick={alRechazar}
        >
          Rechazar
        </Button>
      </SimpleGrid>
    </Box>
  );
};

export default TarjetaOferta;
