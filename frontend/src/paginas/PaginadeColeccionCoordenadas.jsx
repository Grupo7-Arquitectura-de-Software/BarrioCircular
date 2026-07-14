import {
  Badge,
  Box,
  Button,
  Circle,
  Flex,
  HStack,
  SimpleGrid,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import {
  MdOutlineHandshake,
  MdOutlineImage,
  MdOutlineLocationOn,
  MdOutlinePerson,
  MdStar,
  MdVerified,
} from "react-icons/md";
import { LuLeaf } from "react-icons/lu";
import { FaWhatsapp } from "react-icons/fa";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

/**
 * Coordinación de recolección (mockup Entregable 3): resumen de la
 * publicación reservada a la izquierda y recordatorio de WhatsApp a la derecha.
 */
const PaginadeColeccionCoordenadas = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : NAVEGACION_RECOLECTOR}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      etiquetaVolver={esCiudadano ? "Volver al Panel" : "Volver"}
      alVolver={() => navigate(esCiudadano ? "/ciudadano/panel" : -1)}
      mostrarBuscador={false}
      anchoContenido="1160px"
    >
      <SimpleGrid columns={{ base: 1, lg: 5 }} gap={6} alignItems="start">
        {/* Resumen de la publicación */}
        <VStack align="stretch" gap={5} gridColumn={{ lg: "span 2" }}>
          <Box
            bg="fondo.tarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            overflow="hidden"
          >
            <Box position="relative" h="180px" bg="fondo.pagina">
              <Flex h="100%" align="center" justify="center" color="gray.300">
                <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
              </Flex>
              <Badge
                position="absolute"
                top={3}
                right={3}
                bg="fondo.tarjeta"
                color="marca.primario"
                borderRadius="full"
                px={3}
                py={1}
                boxShadow="sm"
              >
                <MdVerified /> Reservado
              </Badge>
            </Box>
            <VStack align="stretch" gap={3} p={5}>
              <Box>
                <Text fontFamily="heading" fontWeight="600" fontSize="lg">
                  Fardos de Cartón Limpio (15kg)
                </Text>
                <Text fontSize="sm" color="gray.600">
                  Publicado hace 2 días por Maria G.
                </Text>
              </Box>
              <HStack
                bg="fondo.pagina"
                border="1px solid"
                borderColor="gray.200"
                borderRadius="lg"
                px={3}
                py={2}
                gap={2}
              >
                <Icono componente={<MdOutlineLocationOn />} tamanio="md" color="marca.primario" />
                <Text fontSize="sm">Distrito La Carolina, Quito</Text>
              </HStack>
              <Text fontSize="sm" color="gray.600">
                Perfecto para mudanzas o proyectos de manualidades. Aplanado y atado con cordel.
                Guardado en interiores y seco.
              </Text>
            </VStack>
          </Box>

          {/* Detalles de recogida */}
          <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
            <HStack gap={2} mb={4}>
              <Icono componente={<MdOutlineHandshake />} tamanio="xl" color="marca.secundario" />
              <Text fontFamily="heading" fontWeight="600" fontSize="lg">
                Detalles de recogida
              </Text>
            </HStack>
            <SimpleGrid
              columns={2}
              bg="fondo.pagina"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="lg"
              p={4}
              gap={2}
              mb={4}
            >
              <Box>
                <Text fontSize="xs" color="gray.500" textTransform="uppercase" fontWeight="600">
                  Tipo de intercambio
                </Text>
                <Text fontSize="sm">Recogida Gratuita</Text>
              </Box>
              <Box>
                <Text fontSize="xs" color="gray.500" textTransform="uppercase" fontWeight="600">
                  Hora
                </Text>
                <Text fontSize="sm">Hoy, Tarde</Text>
              </Box>
            </SimpleGrid>
            <HStack gap={3}>
              <Circle size="40px" bg="fondo.cabeceraTarjeta" color="gray.600">
                <Icono componente={<MdOutlinePerson />} tamanio="xl" />
              </Circle>
              <Box>
                <Text fontWeight="600" fontSize="sm">
                  Carlos R. (Comprador)
                </Text>
                <HStack gap={1} color="gray.600" fontSize="xs">
                  <MdStar color="var(--chakra-colors-yellow-500)" />
                  <Text>4.9 (12 intercambios)</Text>
                </HStack>
              </Box>
            </HStack>
          </Box>

          {/* Impacto */}
          <HStack
            bg="verde.50"
            border="1px solid"
            borderColor="verde.200"
            borderRadius="xl"
            p={4}
            gap={3}
          >
            <Circle size="36px" bg="fondo.tarjeta">
              <Icono componente={<LuLeaf />} tamanio="md" color="marca.primario" />
            </Circle>
            <Box>
              <Text fontWeight="600" fontSize="sm" color="marca.primario">
                Desviando ~15kg del vertedero
              </Text>
              <Text fontSize="xs" color="gray.600">
                ¡Buen trabajo apoyando la economía circular!
              </Text>
            </Box>
          </HStack>

          <Button
            colorPalette="verde"
            bg="marca.primario"
            rounded="lg"
            onClick={() => navigate(`${prefijoRuta}/entregar-material`)}
          >
            Entregar Material
          </Button>
        </VStack>

        {/* WhatsApp Panel */}
        <Box gridColumn={{ lg: "span 3" }} h="100%">
          <Box
            bg="fondo.tarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            p={8}
            h="100%"
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            textAlign="center"
          >
            <Box color="#25D366" bg="green.50" p={6} borderRadius="full" mb={6}>
              <FaWhatsapp size={64} />
            </Box>
            <Text fontFamily="heading" fontWeight="700" fontSize="2xl" mb={3}>
              Coordinación por WhatsApp
            </Text>
            <Text color="gray.600" maxW="400px" mb={6}>
              La coordinación de la entrega se realiza directamente por WhatsApp. El recolector se
              contactará contigo para acordar los detalles.
            </Text>
            <Text fontSize="sm" color="gray.500">
              Recuerda tener a mano el material empacado y tu código QR si el recolector lo
              solicita.
            </Text>
          </Box>
        </Box>
      </SimpleGrid>
    </DiseniodeAplicacion>
  );
};

export default PaginadeColeccionCoordenadas;
