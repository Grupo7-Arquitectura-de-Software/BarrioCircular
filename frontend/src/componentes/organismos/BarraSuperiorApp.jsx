import { Circle, Flex, HStack, Input, InputGroup, Link, Text } from "@chakra-ui/react";
import {
  MdArrowBack,
  MdOutlineAccountCircle,
  MdOutlineNotifications,
  MdOutlineSearch,
} from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

/**
 * Barra superior del panel: enlace de regreso opcional, buscador e iconos.
 */
const BarraSuperiorApp = ({ etiquetaVolver, alVolver, mostrarBuscador = true }) => {
  return (
    <Flex
      as="header"
      px={{ base: 4, md: 6 }}
      py={3}
      gap={4}
      bg="fondo.pagina"
      borderBottom="1px solid"
      borderColor="gray.200"
      align="center"
      justify="space-between"
    >
      <HStack gap={4} flex="1" minW={0}>
        {alVolver && (
          <Link
            onClick={alVolver}
            color={etiquetaVolver ? "gray.700" : "gray.700"}
            fontSize="sm"
            fontWeight="500"
            flexShrink={0}
            display="inline-flex"
            alignItems="center"
            gap={1}
          >
            <MdArrowBack /> <Text as="span">{etiquetaVolver || "Volver"}</Text>
          </Link>
        )}
        {mostrarBuscador && (
          <InputGroup
            maxW="320px"
            startElement={<Icono componente={<MdOutlineSearch />} tamanio="md" color="gray.500" />}
          >
            <Input
              placeholder="Buscar materiales..."
              size="sm"
              bg="fondo.tarjeta"
              borderRadius="full"
              borderColor="gray.200"
            />
          </InputGroup>
        )}
      </HStack>

      <HStack gap={3} color="gray.700" flexShrink={0}>
        <Icono componente={<MdOutlineNotifications />} tamanio="xl" />
        <Circle size="32px" bg="fondo.cabeceraTarjeta">
          <Icono componente={<MdOutlineAccountCircle />} tamanio="xl" color="gray.600" />
        </Circle>
      </HStack>
    </Flex>
  );
};

export default BarraSuperiorApp;
