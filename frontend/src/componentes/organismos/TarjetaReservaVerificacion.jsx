import { Badge, Box, Button, Flex, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { MdOutlineFactCheck, MdOutlineMap, MdOutlineScale } from "react-icons/md";

import { barrioMasCercano, etiquetaTipoResiduo } from "@/utilidades/barriosQuito";
import { ETAPAS_RESERVA } from "@/utilidades/progresoReserva";

// Estado de verificación de una reserva, derivado del EstadoPublicacion del
// backend (documento 04): mientras siga RESERVADA la verificación en sitio
// está pendiente; al FINALIZAR la operación queda verificada.
const ESTADOS_VERIFICACION = {
  RESERVADA: {
    etiqueta: "Pendiente",
    bg: "orange.100",
    color: "orange.700",
    descripcion: "El material aún debe verificarse en el punto de recolección.",
  },
  FINALIZADA: {
    etiqueta: "Verificada",
    bg: "verde.100",
    color: "marca.primario",
    descripcion: "El material fue verificado y la operación quedó completada.",
  },
};

const obtenerEstadoVerificacion = (estado) =>
  ESTADOS_VERIFICACION[estado] || ESTADOS_VERIFICACION.RESERVADA;

const DatoVerificacion = ({ etiqueta, valor, valorColor = "gray.900" }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="lg" p={3}>
    <Text fontSize="xs" color="gray.600" mb={1}>
      {etiqueta}
    </Text>
    <Text fontFamily="heading" fontWeight="700" fontSize="sm" color={valorColor}>
      {valor}
    </Text>
  </Box>
);

/**
 * Tarjeta de una reserva del comprador con su estado de verificación.
 * Cerrada muestra el resumen como card de pendiente; abierta ("ingresar a la
 * reserva") despliega las cards del estado de verificación y las acciones de
 * logística (ver ruta / coordinar) sin perder el contexto de la publicación.
 */
const TarjetaReservaVerificacion = ({
  reserva,
  etapa = ETAPAS_RESERVA.COORDINAR,
  abierta = false,
  alAbrir,
  alCerrar,
  alVerRuta,
  alCoordinar,
  alVerificar,
}) => {
  const estadoVerificacion = obtenerEstadoVerificacion(reserva.estado);
  const titulo = `${reserva.pesoKg}kg de ${etiquetaTipoResiduo(reserva.tipoResiduo)}`;
  const citaConfirmada = etapa === ETAPAS_RESERVA.VERIFICAR;

  return (
    <Box
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor={abierta ? "marca.primario" : "gray.200"}
      borderRadius="lg"
      p={4}
      cursor={abierta ? "default" : "pointer"}
      onClick={abierta ? undefined : alAbrir}
      transition="box-shadow 0.15s ease"
      _hover={abierta ? undefined : { boxShadow: "md" }}
    >
      <Flex justify="space-between" gap={2} mb={1}>
        <Text fontWeight="600" fontSize="sm">
          {titulo}
        </Text>
        <Text fontWeight="700" fontSize="sm" color="marca.primario" flexShrink={0}>
          ${Number(reserva.precioPorKilo).toFixed(2)}/kg
        </Text>
      </Flex>
      <Text fontSize="xs" color="gray.600" mb={2}>
        {barrioMasCercano(reserva.latitud, reserva.longitud)}
      </Text>
      <Flex gap={1} wrap="wrap">
        <Badge bg={estadoVerificacion.bg} color={estadoVerificacion.color} borderRadius="md" px={2}>
          <MdOutlineFactCheck /> Verificación {estadoVerificacion.etiqueta.toLowerCase()}
        </Badge>
        {citaConfirmada && (
          <Badge bg="azul.100" color="marca.secundario" borderRadius="md" px={2}>
            Cita confirmada
          </Badge>
        )}
      </Flex>

      {abierta && (
        <VStack align="stretch" gap={3} mt={4} pt={4} borderTop="1px solid" borderColor="gray.100">
          <Text fontWeight="700" fontSize="sm">
            Estado de verificación
          </Text>
          <SimpleGrid columns={2} gap={2}>
            <DatoVerificacion
              etiqueta="Verificación"
              valor={estadoVerificacion.etiqueta}
              valorColor={estadoVerificacion.color}
            />
            <DatoVerificacion etiqueta="Peso reservado" valor={`${reserva.pesoKg} kg`} />
            <DatoVerificacion
              etiqueta="Precio por kilo"
              valor={`$${Number(reserva.precioPorKilo).toFixed(2)}`}
              valorColor="marca.primario"
            />
            <DatoVerificacion
              etiqueta="Ubicación"
              valor={barrioMasCercano(reserva.latitud, reserva.longitud)}
            />
          </SimpleGrid>
          <Text fontSize="xs" color="gray.600">
            {citaConfirmada
              ? "Cita confirmada. Continúa con la verificación del peso y estado del material."
              : estadoVerificacion.descripcion}
          </Text>
          <VStack align="stretch" gap={2}>
            {citaConfirmada ? (
              <Button
                size="xs"
                colorPalette="verde"
                bg="marca.primario"
                rounded="lg"
                onClick={alVerificar}
              >
                <MdOutlineScale /> Verificar peso y material
              </Button>
            ) : (
              <Button
                size="xs"
                colorPalette="verde"
                bg="marca.primario"
                rounded="lg"
                onClick={alCoordinar}
              >
                Coordinar recolección
              </Button>
            )}
            {alVerRuta && (
              <Button
                size="xs"
                variant="outline"
                colorPalette="verde"
                rounded="lg"
                onClick={alVerRuta}
              >
                <MdOutlineMap /> Ver ruta de recolección
              </Button>
            )}
            <Button size="xs" variant="ghost" colorPalette="gray" rounded="lg" onClick={alCerrar}>
              Cerrar
            </Button>
          </VStack>
        </VStack>
      )}
    </Box>
  );
};

export default TarjetaReservaVerificacion;
