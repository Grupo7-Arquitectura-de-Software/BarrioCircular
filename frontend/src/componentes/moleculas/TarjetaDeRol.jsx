import { Circle, Text, VStack } from "@chakra-ui/react";
import Icono from "../atomos/Icono.jsx";

const TarjetaDeRol = ({ titulo, descripcion, icono, alSeleccionar }) => {
  return (
    <VStack
      as="button"
      type="button"
      onClick={alSeleccionar}
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="lg"
      px={6}
      py={8}
      gap={4}
      w="100%"
      maxW="280px"
      cursor="pointer"
      transition="all 0.2s ease"
      _hover={{ borderColor: "marca.primario", boxShadow: "md", transform: "translateY(-2px)" }}
      _focusVisible={{ outline: "2px solid", outlineColor: "marca.primario", outlineOffset: "2px" }}
    >
      <Circle size="80px" bg="fondo.cabeceraTarjeta" color="marca.primario">
        <Icono componente={icono} tamanio="3xl" color="marca.primario" />
      </Circle>
      <Text fontFamily="heading" fontWeight="600" fontSize="2xl">
        {titulo}
      </Text>
      <Text fontSize="sm" color="gray.600" textAlign="center">
        "{descripcion}"
      </Text>
    </VStack>
  );
};

export default TarjetaDeRol;
