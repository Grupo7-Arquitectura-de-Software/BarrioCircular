import { Badge, Box, Circle, Flex, HStack, Link, Text, VStack } from "@chakra-ui/react";
import { MdCheck } from "react-icons/md";

const PARADAS_EJEMPLO = [
  {
    id: 1,
    titulo: "Centro La Mariscal",
    detalle: "120kg de cartón recogido • 09:15 AM",
    estado: "completado",
  },
  {
    id: 2,
    titulo: "Coop. Centro Histórico",
    detalle: "Esperado: 45kg Vidrio • ETA: 10:30 AM",
    estado: "actual",
  },
  {
    id: 3,
    titulo: "Punto de recolección Guápulo",
    detalle: "Pendiente • Est. 11:45 AM",
    estado: "pendiente",
  },
];

const IndicadorParada = ({ estado }) => {
  if (estado === "completado") {
    return (
      <Circle size="22px" bg="marca.primario" color="white">
        <MdCheck size={14} />
      </Circle>
    );
  }
  if (estado === "actual") {
    return (
      <Circle size="22px" border="2px solid" borderColor="marca.primario" bg="fondo.tarjeta">
        <Circle size="10px" bg="marca.primario" />
      </Circle>
    );
  }
  return <Circle size="22px" bg="fondo.cabeceraTarjeta" />;
};

/**
 * Ruta de recolección del día (mockup "Gestión Híbrida"): mapa con estado en
 * vivo y línea de tiempo de paradas.
 */
const PanelRutaRecoleccion = ({
  paradas = PARADAS_EJEMPLO,
  nombreRuta = "Ruta Q-Sur",
  alVerRuta,
}) => {
  return (
    <Box
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
    >
      {/* TODO: integrar mapa en vivo cuando exista el servicio de rutas. */}
      <Box position="relative" h="220px" bg="fondo.cabeceraTarjeta">
        <Flex h="100%" align="center" justify="center" color="gray.500" fontSize="sm">
          Mapa de la ruta en Quito
        </Flex>
        <HStack position="absolute" top={4} left={4} gap={2}>
          <Badge bg="verde.100" color="marca.primario" borderRadius="full" px={3} py={1}>
            ● En vivo
          </Badge>
          <Badge bg="fondo.tarjeta" borderRadius="full" px={3} py={1} boxShadow="sm">
            {nombreRuta}
          </Badge>
        </HStack>
      </Box>

      <Box p={6}>
        <Flex justify="space-between" align="center" mb={5}>
          <Text fontFamily="heading" fontWeight="700" fontSize="xl">
            Ruta de recolección actual
          </Text>
          <Link fontSize="sm" fontWeight="600" color="marca.secundario" onClick={alVerRuta}>
            Ver ruta completa →
          </Link>
        </Flex>

        <VStack align="stretch" gap={0}>
          {paradas.map((parada, indice) => {
            const esActual = parada.estado === "actual";
            return (
              <Flex key={parada.id} gap={4} align="stretch">
                <VStack gap={0} align="center">
                  <IndicadorParada estado={parada.estado} />
                  {indice < paradas.length - 1 && (
                    <Box flex="1" w="2px" bg="gray.200" minH="24px" />
                  )}
                </VStack>
                <Box
                  flex="1"
                  mb={indice < paradas.length - 1 ? 4 : 0}
                  {...(esActual
                    ? {
                        bg: "fondo.pagina",
                        border: "1px solid",
                        borderColor: "verde.300",
                        borderRadius: "lg",
                        px: 4,
                        py: 2,
                      }
                    : {})}
                >
                  <Text
                    fontWeight="600"
                    fontSize="sm"
                    color={esActual ? "marca.primario" : "gray.800"}
                  >
                    {parada.titulo}
                  </Text>
                  <Text fontSize="sm" color="gray.600">
                    {parada.detalle}
                  </Text>
                </Box>
              </Flex>
            );
          })}
        </VStack>
      </Box>
    </Box>
  );
};

export default PanelRutaRecoleccion;
