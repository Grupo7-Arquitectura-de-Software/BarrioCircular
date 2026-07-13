import { Badge, Box, Button, Circle, Flex, HStack, Text, VStack } from "@chakra-ui/react";
import { MdCheck, MdOutlineAccessTime, MdOutlineLocalShipping } from "react-icons/md";

import { etiquetaTipoResiduo } from "@/utilidades/catalogoMateriales";

const FORMATEADOR_HORA_ECUADOR = new Intl.DateTimeFormat("es-EC", {
  hour: "2-digit",
  minute: "2-digit",
  timeZone: "America/Guayaquil",
});

const ESTADO_PARADA = {
  COMPLETADA: {
    etiqueta: "Completada",
    color: "verde",
  },
  EN_CURSO: {
    etiqueta: "En curso",
    color: "azul",
  },
  EN_PROGRESO: {
    etiqueta: "En progreso",
    color: "azul",
  },
  PENDIENTE: {
    etiqueta: "Pendiente",
    color: "gray",
  },
};

const formatearHora = (fechaHora) => {
  if (!fechaHora) return "Sin registrar";
  return FORMATEADOR_HORA_ECUADOR.format(new Date(fechaHora));
};

const obtenerConfiguracionEstado = (estado) =>
  ESTADO_PARADA[estado] || {
    etiqueta: estado || "Sin estado",
    color: "gray",
  };

const IndicadorParada = ({ estado }) => {
  if (estado === "COMPLETADA") {
    return (
      <Circle size="28px" bg="marca.primario" color="white">
        <MdCheck size={16} />
      </Circle>
    );
  }
  if (estado === "EN_CURSO") {
    return (
      <Circle size="28px" border="2px solid" borderColor="marca.primario" bg="fondo.tarjeta">
        <MdOutlineLocalShipping size={16} color="var(--chakra-colors-marca-primario)" />
      </Circle>
    );
  }
  if (estado === "EN_PROGRESO") {
    return (
      <Circle size="28px" border="2px solid" borderColor="marca.primario" bg="fondo.tarjeta">
        <MdOutlineLocalShipping size={16} color="var(--chakra-colors-marca-primario)" />
      </Circle>
    );
  }
  return (
    <Circle size="28px" bg="fondo.cabeceraTarjeta" color="gray.500">
      <MdOutlineAccessTime size={15} />
    </Circle>
  );
};

const ESTADOS_PARA_REGISTRAR = ["PENDIENTE", "EN_PROGRESO"];

const TimelineRutaRecoleccion = ({
  paradas = [],
  onRegistrarLlegada,
  registrandoLlegada = false,
  paradaRegistrando = null,
}) => {
  if (paradas.length === 0) {
    return (
      <Box border="1px dashed" borderColor="gray.300" borderRadius="lg" py={10} textAlign="center">
        <Text fontWeight="600">No hay paradas para mostrar</Text>
        <Text fontSize="sm" color="gray.600">
          Cuando exista una ruta activa, sus paradas aparecerán aquí.
        </Text>
      </Box>
    );
  }

  return (
    <VStack align="stretch" gap={0}>
      {paradas.map((parada, indice) => {
        const estado = obtenerConfiguracionEstado(parada.estado);
        return (
          <Flex key={parada.paradaId || parada.publicacionId || parada.orden} gap={4}>
            <VStack gap={0} align="center">
              <IndicadorParada estado={parada.estado} />
              {indice < paradas.length - 1 && <Box flex="1" w="2px" bg="gray.200" minH="56px" />}
            </VStack>

            <Box
              flex="1"
              bg={["EN_CURSO", "EN_PROGRESO"].includes(parada.estado) ? "fondo.pagina" : "transparent"}
              border={["EN_CURSO", "EN_PROGRESO"].includes(parada.estado) ? "1px solid" : "none"}
              borderColor="verde.300"
              borderRadius="lg"
              px={["EN_CURSO", "EN_PROGRESO"].includes(parada.estado) ? 4 : 0}
              py={["EN_CURSO", "EN_PROGRESO"].includes(parada.estado) ? 3 : 0}
              mb={indice < paradas.length - 1 ? 5 : 0}
            >
              <Flex
                align={{ base: "flex-start", md: "center" }}
                justify="space-between"
                gap={3}
                direction={{ base: "column", md: "row" }}
              >
                <Box>
                  <Text fontWeight="700" color="gray.900">
                    Parada {parada.orden}: {etiquetaTipoResiduo(parada.tipoResiduo)}
                  </Text>
                  <HStack gap={2} mt={1} wrap="wrap">
                    <Text fontSize="sm" color="gray.600">
                      {Number(parada.pesoKg || 0).toFixed(1)} kg
                    </Text>
                    <Text fontSize="sm" color="gray.400">
                      •
                    </Text>
                    <Text fontSize="sm" color="gray.600">
                      Estimada: {formatearHora(parada.horaLlegadaEstimada)}
                    </Text>
                    {parada.horaLlegadaReal && (
                      <>
                        <Text fontSize="sm" color="gray.400">
                          •
                        </Text>
                        <Text fontSize="sm" color="gray.600">
                          Real: {formatearHora(parada.horaLlegadaReal)}
                        </Text>
                      </>
                    )}
                  </HStack>
                </Box>

                <Badge colorPalette={estado.color} borderRadius="full" px={3} py={1}>
                  {estado.etiqueta}
                </Badge>
              </Flex>

              {ESTADOS_PARA_REGISTRAR.includes(parada.estado) && onRegistrarLlegada && (
                <Button
                  mt={4}
                  size="sm"
                  colorPalette="verde"
                  rounded="lg"
                  loadingText="Registrando"
                  isLoading={registrandoLlegada && paradaRegistrando === parada.paradaId}
                  onClick={() => onRegistrarLlegada(parada)}
                >
                  {parada.estado === "EN_PROGRESO" ? "Continuar verificación" : "Registrar llegada"}
                </Button>
              )}
            </Box>
          </Flex>
        );
      })}
    </VStack>
  );
};

export default TimelineRutaRecoleccion;
