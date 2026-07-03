import { Badge, Box, Button, Flex, HStack, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { MdOutlineImage, MdOutlineLocationOn, MdOutlineSell, MdVerified } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";
import TarjetaVendedor from "../moleculas/TarjetaVendedor.jsx";

const DatoMaterial = ({ etiqueta, valor, valorColor = "gray.900" }) => (
  <Box bg="fondo.cabeceraTarjeta" borderRadius="lg" p={4}>
    <Text fontSize="xs" color="gray.600" mb={1}>
      {etiqueta}
    </Text>
    <Text fontFamily="heading" fontWeight="700" color={valorColor}>
      {valor}
    </Text>
  </Box>
);

/**
 * Detalle de material publicado (mockup "Detalle de Material"): imagen con
 * estado, información del material, vendedor y ubicación con acción de oferta.
 */
const DetallePublicacion = ({
  tipoMaterial = "Cartón Corrugado",
  pesoKg = 450,
  precioPorKilo = "$0.18",
  descripcion = "Aplanado y flejado. Gran volumen disponible semanalmente de un colectivo minorista local. El material está limpio y libre de contaminantes mayores, ideal para procesamiento directo.",
  estado = "Disponible",
  vendedor = "Colectivo La Carolina",
  rotuloVendedor = "VENDEDOR (COLECTIVO)",
  calificacionVendedor = "4.9",
  detalleCalificacion = "(85 transacciones)",
  ubicacion = "Sector La Carolina, Quito",
  alRealizarOferta,
}) => {
  return (
    <SimpleGrid columns={{ base: 1, lg: 3 }} gap={6} alignItems="start">
      {/* Columna principal */}
      <VStack align="stretch" gap={6} gridColumn={{ lg: "span 2" }}>
        <Box
          position="relative"
          h="340px"
          bg="fondo.tarjeta"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="xl"
          overflow="hidden"
        >
          <Flex h="100%" align="center" justify="center" color="gray.300">
            <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
          </Flex>
          <Badge
            position="absolute"
            top={4}
            left={4}
            bg="marca.primario"
            color="white"
            borderRadius="full"
            px={3}
            py={1}
          >
            <MdVerified /> {estado}
          </Badge>
        </Box>

        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
          <Text fontFamily="heading" fontWeight="700" fontSize="xl" mb={5}>
            Información del Material
          </Text>
          <SimpleGrid columns={{ base: 1, md: 3 }} gap={4} mb={5}>
            <DatoMaterial etiqueta="Tipo de Material" valor={tipoMaterial} />
            <DatoMaterial etiqueta="Peso Estimado" valor={`${pesoKg} kg`} />
            <DatoMaterial
              etiqueta="Precio Referencial"
              valor={`${precioPorKilo}/kg`}
              valorColor="marca.primario"
            />
          </SimpleGrid>
          <Text fontSize="sm" color="gray.600" borderTop="1px solid" borderColor="gray.100" pt={4}>
            {descripcion}
          </Text>
        </Box>
      </VStack>

      {/* Columna lateral */}
      <VStack align="stretch" gap={5}>
        <TarjetaVendedor
          rotulo={rotuloVendedor}
          nombre={vendedor}
          calificacion={calificacionVendedor}
          detalleCalificacion={detalleCalificacion}
        />

        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
          <HStack gap={2} mb={1}>
            <Icono componente={<MdOutlineLocationOn />} tamanio="lg" color="marca.primario" />
            <Text fontFamily="heading" fontWeight="700">
              Ubicación del Material
            </Text>
          </HStack>
          <Text fontSize="sm" color="gray.600" mb={3}>
            {ubicacion}
          </Text>
          {/* TODO: integrar mapa (Leaflet/Google Maps) cuando esté disponible. */}
          <Flex
            h="220px"
            bg="fondo.cabeceraTarjeta"
            borderRadius="lg"
            align="center"
            justify="center"
            color="gray.500"
            fontSize="sm"
          >
            Mapa del sector
          </Flex>
        </Box>

        <Button
          size="lg"
          colorPalette="verde"
          bg="marca.primario"
          rounded="xl"
          onClick={alRealizarOferta}
        >
          <MdOutlineSell /> Enviar Oferta
        </Button>
      </VStack>
    </SimpleGrid>
  );
};

export default DetallePublicacion;
