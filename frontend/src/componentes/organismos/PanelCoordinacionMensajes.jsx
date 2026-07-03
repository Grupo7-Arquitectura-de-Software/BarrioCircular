import { Badge, Box, Circle, Flex, HStack, Link, Text, VStack } from "@chakra-ui/react";
import { MdOutlineForum } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const MENSAJES_EJEMPLO = [
  {
    id: 1,
    remitente: "Carlos V. (Conductor)",
    texto: "Tráfico en la 6 de Diciembre, retraso de 10 min.",
    hora: "10:28 AM",
    nuevo: true,
  },
  {
    id: 2,
    remitente: "Maria S. (Gerente de Centro)",
    texto: "La nueva prensa de PET está operativa. Envía cargas.",
    hora: "Ayer",
    nuevo: false,
  },
  {
    id: 3,
    remitente: "EcoGroup Quito",
    texto: "Factura #4402 recibida. Gracias.",
    hora: "Lun",
    nuevo: false,
  },
];

const obtenerIniciales = (nombre = "") =>
  nombre
    .split(" ")
    .map((palabra) => palabra[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase();

/**
 * Panel de mensajes de coordinación (mockup "Gestión Híbrida").
 */
const PanelCoordinacionMensajes = ({ mensajes = MENSAJES_EJEMPLO, alVerTodos }) => {
  const nuevos = mensajes.filter((mensaje) => mensaje.nuevo).length;

  return (
    <Flex
      direction="column"
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
      h="100%"
    >
      <Flex
        px={5}
        py={4}
        borderBottom="1px solid"
        borderColor="gray.200"
        justify="space-between"
        align="center"
      >
        <HStack gap={2}>
          <Icono componente={<MdOutlineForum />} tamanio="lg" color="marca.primario" />
          <Text fontFamily="heading" fontWeight="700" fontSize="lg">
            Coordinación
          </Text>
        </HStack>
        {nuevos > 0 && (
          <Badge bg="fondo.cabeceraTarjeta" color="marca.secundario" borderRadius="full" px={2}>
            {nuevos} Nuevos
          </Badge>
        )}
      </Flex>

      <VStack align="stretch" gap={0} flex="1" py={2}>
        {mensajes.map((mensaje) => (
          <Flex
            key={mensaje.id}
            px={5}
            py={3}
            gap={3}
            bg={mensaje.nuevo ? "fondo.cabeceraTarjeta" : "transparent"}
            borderLeft={mensaje.nuevo ? "3px solid" : "3px solid transparent"}
            borderLeftColor={mensaje.nuevo ? "marca.primario" : "transparent"}
          >
            <Circle
              size="38px"
              bg="fondo.pagina"
              border="1px solid"
              borderColor="gray.200"
              fontSize="xs"
              fontWeight="700"
              color="gray.600"
              flexShrink={0}
            >
              {obtenerIniciales(mensaje.remitente)}
            </Circle>
            <Box flex="1" minW={0}>
              <Flex justify="space-between" gap={2}>
                <Text fontWeight="600" fontSize="sm">
                  {mensaje.remitente}
                </Text>
                <Text
                  fontSize="xs"
                  color={mensaje.nuevo ? "marca.secundario" : "gray.500"}
                  flexShrink={0}
                >
                  {mensaje.hora}
                </Text>
              </Flex>
              <Text fontSize="sm" color="gray.600">
                {mensaje.texto}
              </Text>
            </Box>
          </Flex>
        ))}
      </VStack>

      <Flex justify="center" borderTop="1px solid" borderColor="gray.200" py={3}>
        <Link fontSize="sm" fontWeight="600" color="marca.primario" onClick={alVerTodos}>
          Ver todos los mensajes
        </Link>
      </Flex>
    </Flex>
  );
};

export default PanelCoordinacionMensajes;
