import { Badge, Box, Circle, Flex, HStack, Link, Spinner, Text, VStack } from "@chakra-ui/react";
import { MdCheck, MdOutlineAccessTime, MdOutlineLocalShipping } from "react-icons/md";

import { etiquetaTipoResiduo } from "@/utilidades/catalogoMateriales";

const FORMATEADOR_HORA_ECUADOR = new Intl.DateTimeFormat("es-EC", {
  hour: "2-digit",
  minute: "2-digit",
  timeZone: "America/Guayaquil",
});

const ESTADOS_RUTA = {
  PLANIFICADA: { etiqueta: "Planificada", color: "azul" },
  EN_CURSO: { etiqueta: "En curso", color: "verde" },
  COMPLETADA: { etiqueta: "Completada", color: "verde" },
  CANCELADA: { etiqueta: "Cancelada", color: "red" },
};

const formatearHora = (fechaHora) => {
  if (!fechaHora) return "Sin hora";
  return FORMATEADOR_HORA_ECUADOR.format(new Date(fechaHora));
};

const obtenerEstadoRuta = (estado) =>
  ESTADOS_RUTA[estado] || { etiqueta: "Sin ruta", color: "gray" };

const obtenerParadaActual = (paradas) =>
  paradas.find((parada) => parada.estado === "EN_PROGRESO") ||
  paradas.find((parada) => parada.estado === "EN_CURSO") ||
  paradas.find((parada) => parada.estado === "PENDIENTE") ||
  paradas[0];

const IndicadorParada = ({ estado }) => {
  if (estado === "COMPLETADA") {
    return (
      <Circle size="22px" bg="marca.primario" color="white">
        <MdCheck size={14} />
      </Circle>
    );
  }
  if (estado === "EN_CURSO") {
    return (
      <Circle size="22px" border="2px solid" borderColor="marca.primario" bg="fondo.tarjeta">
        <MdOutlineLocalShipping size={13} color="var(--chakra-colors-marca-primario)" />
      </Circle>
    );
  }
  if (estado === "EN_PROGRESO") {
    return (
      <Circle size="22px" border="2px solid" borderColor="marca.primario" bg="fondo.tarjeta">
        <MdOutlineLocalShipping size={13} color="var(--chakra-colors-marca-primario)" />
      </Circle>
    );
  }
  return (
    <Circle size="22px" bg="fondo.cabeceraTarjeta" color="gray.500">
      <MdOutlineAccessTime size={13} />
    </Circle>
  );
};

const PanelRutaRecoleccion = ({ ruta, cargando = false, mensajeError = "", alVerRuta }) => {
  const paradas = ruta?.paradas || [];
  const estadoRuta = obtenerEstadoRuta(ruta?.estado);
  const paradasResumen = paradas.slice(0, 3);
  const paradaActual = obtenerParadaActual(paradas);

  return (
    <Box
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
    >
      <Box bg="fondo.cabeceraTarjeta" p={5}>
        <Flex justify="space-between" align="flex-start" gap={4}>
          <Box>
            <Text fontSize="sm" color="gray.600">
              Ruta del día
            </Text>
            <Text fontFamily="heading" fontWeight="700" fontSize="xl">
              {ruta ? `${paradas.length} paradas programadas` : "Sin ruta activa"}
            </Text>
          </Box>
          <Badge colorPalette={estadoRuta.color} borderRadius="full" px={3} py={1}>
            {estadoRuta.etiqueta}
          </Badge>
        </Flex>

        {paradaActual && (
          <Box mt={4} bg="fondo.tarjeta" borderRadius="lg" p={4}>
            <Text fontSize="xs" color="gray.600" fontWeight="600">
              Próxima parada
            </Text>
            <Text fontWeight="700">
              {etiquetaTipoResiduo(paradaActual.tipoResiduo)} ·{" "}
              {Number(paradaActual.pesoKg || 0).toFixed(1)} kg
            </Text>
            <Text fontSize="sm" color="gray.600">
              ETA {formatearHora(paradaActual.horaLlegadaEstimada)}
            </Text>
          </Box>
        )}
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

        {cargando ? (
          <Flex justify="center" py={8}>
            <Spinner color="marca.primario" />
          </Flex>
        ) : paradasResumen.length > 0 ? (
          <VStack align="stretch" gap={0}>
            {paradasResumen.map((parada, indice) => {
              const esActual = parada.estado === "EN_CURSO" || parada.estado === "EN_PROGRESO";
              return (
                <Flex key={parada.paradaId || parada.publicacionId} gap={4} align="stretch">
                  <VStack gap={0} align="center">
                    <IndicadorParada estado={parada.estado} />
                    {indice < paradasResumen.length - 1 && (
                      <Box flex="1" w="2px" bg="gray.200" minH="24px" />
                    )}
                  </VStack>
                  <Box
                    flex="1"
                    mb={indice < paradasResumen.length - 1 ? 4 : 0}
                    bg={esActual ? "fondo.pagina" : "transparent"}
                    border={esActual ? "1px solid" : "none"}
                    borderColor="verde.300"
                    borderRadius="lg"
                    px={esActual ? 4 : 0}
                    py={esActual ? 2 : 0}
                  >
                    <Text
                      fontWeight="600"
                      fontSize="sm"
                      color={esActual ? "marca.primario" : "gray.800"}
                    >
                      Parada {parada.orden}: {etiquetaTipoResiduo(parada.tipoResiduo)}
                    </Text>
                    <HStack gap={2} wrap="wrap">
                      <Text fontSize="sm" color="gray.600">
                        {Number(parada.pesoKg || 0).toFixed(1)} kg
                      </Text>
                      <Text fontSize="sm" color="gray.400">
                        ·
                      </Text>
                      <Text fontSize="sm" color="gray.600">
                        ETA {formatearHora(parada.horaLlegadaEstimada)}
                      </Text>
                    </HStack>
                  </Box>
                </Flex>
              );
            })}
          </VStack>
        ) : (
          <Box
            border="1px dashed"
            borderColor="gray.300"
            borderRadius="lg"
            py={8}
            px={4}
            textAlign="center"
          >
            <Text fontWeight="600">No hay ruta activa</Text>
            <Text fontSize="sm" color="gray.600">
              {mensajeError || "Construye tu ruta para ver las paradas del día."}
            </Text>
          </Box>
        )}
      </Box>
    </Box>
  );
};

export default PanelRutaRecoleccion;
