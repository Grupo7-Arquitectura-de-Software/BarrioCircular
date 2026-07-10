import { Badge, Box, Button, Flex, Spinner, Text, VStack } from "@chakra-ui/react";
import { useEffect, useRef, useState } from "react";
import { MdOutlineAddRoad, MdOutlineLocalShipping } from "react-icons/md";

import { toaster } from "@/components/ui/toaster-instance";
import Icono from "@/componentes/atomos/Icono.jsx";
import MapaRutaRecoleccion from "@/componentes/organismos/MapaRutaRecoleccion.jsx";
import TimelineRutaRecoleccion from "@/componentes/organismos/TimelineRutaRecoleccion.jsx";
import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion.jsx";
import { useRutaRecoleccion } from "@/utilidades/useRutaRecoleccion";
import { NAVEGACION_RECOLECTOR, SUBTITULO_RECOLECTOR } from "@/utilidades/navegacionPanel";

const ZONA_OPERATIVA = "America/Guayaquil";

const fechaActualEcuador = () =>
  new Date().toLocaleDateString("en-CA", {
    timeZone: ZONA_OPERATIVA,
  });

const horaActualEcuador = () =>
  new Date().toLocaleTimeString("en-GB", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
    timeZone: ZONA_OPERATIVA,
  });

const ESTADOS_RUTA = {
  PLANIFICADA: {
    etiqueta: "Planificada",
    color: "azul",
  },
  EN_CURSO: {
    etiqueta: "En curso",
    color: "verde",
  },
  COMPLETADA: {
    etiqueta: "Completada",
    color: "verde",
  },
  CANCELADA: {
    etiqueta: "Cancelada",
    color: "red",
  },
};

const obtenerEstadoRuta = (estado) =>
  ESTADOS_RUTA[estado] || {
    etiqueta: estado || "Sin ruta",
    color: "gray",
  };

const PaginaRutaRecoleccion = () => {
  const {
    ruta,
    cargando,
    mensajeError,
    construyendo,
    registrandoLlegada,
    actualizandoRuta,
    cargarRutaActiva,
    construirRuta,
    actualizarRuta,
    registrarLlegada,
  } = useRutaRecoleccion();
  const [paradaRegistrando, setParadaRegistrando] = useState(null);
  const cargaInicialRef = useRef(false);

  const manejarActualizarRuta = async () => {
    const rutaActualizada = await actualizarRuta();
    if (rutaActualizada) {
      toaster.create({
        title: "Ruta actualizada",
        description: "La ruta de recolección se sincronizó con las reservas vigentes.",
        type: "success",
        duration: 3000,
      });
    } else {
      toaster.create({
        title: "No se pudo actualizar la ruta",
        description: mensajeError || "Intenta de nuevo más tarde.",
        type: "error",
        duration: 4500,
      });
    }
  };

  useEffect(() => {
    if (cargaInicialRef.current) return;
    cargaInicialRef.current = true;
    cargarRutaActiva();
  }, [cargarRutaActiva]);

  const manejarConstruirRuta = () => {
    construirRuta({
      fechaRuta: fechaActualEcuador(),
      horaInicioRuta: horaActualEcuador(),
    });
  };

  const estadoRuta = obtenerEstadoRuta(ruta?.estado);
  const paradas = ruta?.paradas || [];

  const manejarRegistrarLlegada = async (paradaId) => {
    if (!ruta?.rutaId) return;

    setParadaRegistrando(paradaId);
    const datosLlegada = {
      fechaLlegada: fechaActualEcuador(),
      horaLlegada: horaActualEcuador(),
    };
    const rutaActualizada = await registrarLlegada(ruta.rutaId, paradaId, datosLlegada);
    if (rutaActualizada) {
      toaster.create({
        title: "Llegada registrada",
        description: "El estado de la ruta se actualizó correctamente.",
        type: "success",
        duration: 3000,
      });
    } else {
      toaster.create({
        title: "No se pudo registrar la llegada",
        description: mensajeError || "Intenta de nuevo más tarde.",
        type: "error",
        duration: 4500,
      });
    }
    setParadaRegistrando(null);
  };

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_RECOLECTOR}
      subtituloMarca={SUBTITULO_RECOLECTOR}
      rutaNuevaPublicacion="/recolector/vender/crear-publicacion"
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={6}>
        <Flex
          justify="space-between"
          align={{ base: "stretch", md: "center" }}
          gap={4}
          direction={{ base: "column", md: "row" }}
        >
          <VStack align="stretch" gap={1}>
            <Text
              fontFamily="heading"
              fontWeight="700"
              fontSize={{ base: "2xl", md: "3xl" }}
              color="marca.primario"
            >
              Ruta de recolección
            </Text>
            <Text color="gray.600">
              Revisa el orden de tus paradas y el material reservado para recoger.
            </Text>
          </VStack>

          <Flex gap={3} align="center" wrap="wrap">
            {ruta && (
              <Badge colorPalette={estadoRuta.color} borderRadius="full" px={4} py={2}>
                {estadoRuta.etiqueta}
              </Badge>
            )}
            {ruta && (
              <Button
                colorPalette="azul"
                rounded="lg"
                size="sm"
                loadingText="Actualizando"
                isLoading={actualizandoRuta}
                onClick={manejarActualizarRuta}
              >
                Actualizar ruta
              </Button>
            )}
          </Flex>
        </Flex>

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : ruta ? (
          <>
            <Flex gap={4} direction={{ base: "column", md: "row" }}>
              <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5} flex="1">
                <Flex gap={3} align="center">
                  <Box bg="fondo.cabeceraTarjeta" borderRadius="lg" color="marca.primario" p={3}>
                    <Icono componente={<MdOutlineLocalShipping />} tamanio="2xl" />
                  </Box>
                  <Box>
                    <Text fontSize="sm" color="gray.600">
                      Estado
                    </Text>
                    <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                      {estadoRuta.etiqueta}
                    </Text>
                  </Box>
                </Flex>
              </Box>

              <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5} flex="1">
                <Text fontSize="sm" color="gray.600">
                  Paradas
                </Text>
                <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                  {paradas.length}
                </Text>
              </Box>
            </Flex>

            <MapaRutaRecoleccion origen={ruta.origen} paradas={paradas} />

            <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
              <Flex justify="space-between" align="center" mb={5} gap={3}>
                <Box>
                  <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                    Timeline de paradas
                  </Text>
                  <Text fontSize="sm" color="gray.600">
                    Horarios mostrados en hora de Ecuador.
                  </Text>
                </Box>
              </Flex>
              <TimelineRutaRecoleccion
                paradas={paradas}
                onRegistrarLlegada={manejarRegistrarLlegada}
                registrandoLlegada={registrandoLlegada}
                paradaRegistrando={paradaRegistrando}
              />
            </Box>
          </>
        ) : (
          <Box bg="fondo.tarjeta" border="1px dashed" borderColor="gray.300" borderRadius="xl" p={8}>
            <VStack gap={4} textAlign="center">
              <Box bg="fondo.cabeceraTarjeta" borderRadius="lg" color="marca.primario" p={4}>
                <Icono componente={<MdOutlineAddRoad />} tamanio="3xl" />
              </Box>
              <VStack gap={1}>
                <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                  No tienes una ruta activa
                </Text>
                <Text color="gray.600">
                  {mensajeError || "Construye una ruta con tus reservas activas para empezar el recorrido."}
                </Text>
              </VStack>
              <Button
                colorPalette="verde"
                bg="marca.primario"
                rounded="lg"
                loading={construyendo}
                loadingText="Construyendo"
                onClick={manejarConstruirRuta}
              >
                Construir ruta de hoy
              </Button>
            </VStack>
          </Box>
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaRutaRecoleccion;
