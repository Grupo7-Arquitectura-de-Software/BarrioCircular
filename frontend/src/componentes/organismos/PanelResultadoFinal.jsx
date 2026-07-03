import { Box, Circle, Flex, Text, VStack } from "@chakra-ui/react";
import { MdCheck } from "react-icons/md";
import Boton from "../atomos/Boton";

const TONOS = {
  verde: { circulo: "marca.primarioContenedor", icono: "marca.primario", titulo: "marca.primario" },
  ambar: { circulo: "orange.500", icono: "orange.500", titulo: "orange.600" },
};

/**
 * Panel de resultado de operación (mockups Entregables 3-4): círculo de
 * estado, resumen de la publicación y monto destacado. `tono` permite la
 * variante ámbar de "Oferta Enviada".
 */
const PanelResultadoFinal = ({
  titulo = "Operación Completada",
  subtitulo = "La transacción ha sido registrada exitosamente en tu historial.",
  tono = "verde",
  tituloResumen = "Resumen de la publicación",
  etiquetaMonto = "Monto",
  tipoMaterial,
  pesoKg,
  monto,
  transaccionId = null,
  etiquetaVolver = "Volver a Ofertas",
  alVolver,
}) => {
  const colores = TONOS[tono] || TONOS.verde;

  return (
    <VStack gap={6} align="stretch" w="100%">
      <VStack gap={4} textAlign="center">
        <Circle size="96px" bg={colores.circulo} boxShadow="lg">
          <Circle size="40px" bg="white" color={colores.icono}>
            <MdCheck size={26} />
          </Circle>
        </Circle>
        <Text
          fontFamily="heading"
          fontWeight="700"
          fontSize={{ base: "2xl", md: "3xl" }}
          color={colores.titulo}
          lineHeight="1.2"
        >
          {titulo}
        </Text>
        <Text color="gray.600" maxW="320px">
          {subtitulo}
        </Text>
        {transaccionId && (
          <Text fontSize="sm" color="gray.500">
            Transacción ID: {transaccionId}
          </Text>
        )}
      </VStack>

      {tipoMaterial && (
        <Box bg="fondo.cabeceraTarjeta" borderRadius="xl" p={5}>
          <Text
            fontSize="xs"
            fontWeight="700"
            color="gray.600"
            textTransform="uppercase"
            letterSpacing="wide"
            mb={3}
          >
            {tituloResumen}
          </Text>
          <VStack gap={0} align="stretch">
            <Flex justify="space-between" py={2} borderBottom="1px solid" borderColor="gray.200">
              <Text color="gray.600">Material</Text>
              <Text fontWeight="700">{tipoMaterial}</Text>
            </Flex>
            <Flex justify="space-between" py={2} borderBottom="1px solid" borderColor="gray.200">
              <Text color="gray.600">Peso</Text>
              <Text fontWeight="700">{pesoKg} kg</Text>
            </Flex>
            {monto && (
              <Flex justify="space-between" bg="verde.100" borderRadius="lg" px={3} py={3} mt={3}>
                <Text fontWeight="600" color="marca.primario">
                  {etiquetaMonto}
                </Text>
                <Text fontFamily="heading" fontWeight="700" fontSize="xl" color="marca.primario">
                  {monto}
                </Text>
              </Flex>
            )}
          </VStack>
        </Box>
      )}

      {alVolver && (
        <Boton texto={etiquetaVolver} variante="solid" ancho="full" alHacer={alVolver} />
      )}
    </VStack>
  );
};

export default PanelResultadoFinal;
