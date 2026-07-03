import { Badge, Box, Flex, HStack, Link, Square, Text } from "@chakra-ui/react";
import Icono from "../atomos/Icono.jsx";

const ACENTOS = {
  verde: { fondo: "verde.50", borde: "verde.200", icono: "marca.primario", cajaIcono: "verde.100" },
  azul: {
    fondo: "fondo.tarjeta",
    borde: "gray.200",
    icono: "marca.secundario",
    cajaIcono: "azul.50",
  },
  neutro: {
    fondo: "fondo.tarjeta",
    borde: "gray.200",
    icono: "gray.700",
    cajaIcono: "fondo.cabeceraTarjeta",
  },
};

/**
 * Tarjeta de estadística del panel (ej. "Total Reciclado — 245 kg").
 */
const TarjetaEstadistica = ({
  icono,
  etiqueta,
  valor,
  unidad,
  acento = "neutro",
  insignia,
  etiquetaAccion,
  alAccionar,
}) => {
  const estilo = ACENTOS[acento] || ACENTOS.neutro;

  return (
    <Box
      bg={estilo.fondo}
      border="1px solid"
      borderColor={estilo.borde}
      borderRadius="xl"
      p={5}
      flex="1"
    >
      <Flex justify="space-between" align="flex-start" mb={5}>
        <Square size="44px" bg={estilo.cajaIcono} borderRadius="lg" color={estilo.icono}>
          <Icono componente={icono} tamanio="xl" color={estilo.icono} />
        </Square>
        {insignia && (
          <Badge colorPalette="verde" variant="subtle" borderRadius="full" px={2}>
            {insignia}
          </Badge>
        )}
      </Flex>
      <Text fontSize="sm" color="gray.600" mb={1}>
        {etiqueta}
      </Text>
      <HStack justify="space-between" align="flex-end">
        <HStack align="baseline" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize="3xl" lineHeight="1">
            {valor}
          </Text>
          {unidad && (
            <Text fontSize="md" color="gray.600">
              {unidad}
            </Text>
          )}
        </HStack>
        {etiquetaAccion && (
          <Link fontSize="sm" fontWeight="600" color="marca.secundario" onClick={alAccionar}>
            {etiquetaAccion} →
          </Link>
        )}
      </HStack>
    </Box>
  );
};

export default TarjetaEstadistica;
