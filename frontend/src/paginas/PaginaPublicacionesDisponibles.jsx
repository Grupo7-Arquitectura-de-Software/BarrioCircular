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
  MdOutlineDelete,
  MdOutlineEdit,
  MdOutlineImage,
  MdOutlineInbox,
  MdOutlineInventory2,
  MdOutlineLocationOn,
  MdOutlinePayments,
  MdOutlineScale,
} from "react-icons/md";
import { LuLeaf } from "react-icons/lu";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

const DetalleItem = ({ etiqueta, icono, valor, valorColor = "gray.800" }) => (
  <Box>
    <Text fontSize="sm" color="gray.500" mb={1}>
      {etiqueta}
    </Text>
    <HStack gap={1} color={valorColor} fontWeight="600">
      <Icono componente={icono} tamanio="md" />
      <Text>{valor}</Text>
    </HStack>
  </Box>
);

/**
 * Detalle de una publicación recién creada (mockup "Publicación Creada").
 */
const PaginaPublicacionesDisponibles = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : []}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      etiquetaVolver="Volver a Publicaciones"
      alVolver={() => navigate(esCiudadano ? "/ciudadano/panel" : -1)}
    >
      <VStack align="stretch" gap={6}>
        {/* Encabezado */}
        <Flex justify="space-between" align="flex-start" wrap="wrap" gap={3}>
          <Box>
            <HStack gap={3} mb={1}>
              <Badge bg="marca.primario" color="white" borderRadius="full" px={3}>
                Disponible
              </Badge>
              <Text fontSize="sm" color="gray.600">
                Publicado hace 2 horas
              </Text>
            </HStack>
            <Text fontFamily="heading" fontWeight="600" fontSize="xl">
              15kg de Cartón Corrugado
            </Text>
          </Box>
          <HStack gap={2}>
            <Button variant="outline" colorPalette="gray" rounded="lg" size="sm">
              <MdOutlineEdit /> Editar Publicación
            </Button>
            <Button variant="ghost" colorPalette="red" rounded="lg" size="sm">
              <MdOutlineDelete /> Eliminar
            </Button>
          </HStack>
        </Flex>

        <SimpleGrid columns={{ base: 1, lg: 3 }} gap={6}>
          {/* Columna principal */}
          <VStack align="stretch" gap={6} gridColumn={{ lg: "span 2" }}>
            <Flex
              h="320px"
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              align="center"
              justify="center"
              color="gray.300"
            >
              <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
            </Flex>

            <Box
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              p={6}
            >
              <Text fontFamily="heading" fontWeight="600" fontSize="lg" mb={5}>
                Detalles de la Publicación
              </Text>
              <SimpleGrid columns={{ base: 2, md: 4 }} gap={4}>
                <DetalleItem
                  etiqueta="Tipo de Material"
                  icono={<MdOutlineInventory2 />}
                  valor="Cartón"
                />
                <DetalleItem etiqueta="Peso Estimado" icono={<MdOutlineScale />} valor="15 kg" />
                <DetalleItem etiqueta="Ubicación" icono={<MdOutlineLocationOn />} valor="Cumbayá" />
                <DetalleItem
                  etiqueta="Precio de Referencia"
                  icono={<MdOutlinePayments />}
                  valor="$5.00"
                  valorColor="marca.primario"
                />
              </SimpleGrid>
            </Box>
          </VStack>

          {/* Columna lateral */}
          <VStack align="stretch" gap={6}>
            <Box bg="verde.50" border="1px solid" borderColor="verde.200" borderRadius="xl" p={5}>
              <HStack gap={2} mb={2}>
                <Circle size="32px" bg="fondo.tarjeta">
                  <Icono componente={<LuLeaf />} tamanio="md" color="marca.primario" />
                </Circle>
                <Text fontWeight="600" color="marca.primario">
                  Impacto Potencial
                </Text>
              </HStack>
              <Text fontSize="sm" color="gray.700">
                Reciclar este cartón ahorra aproximadamente 10 litros de agua y reduce los residuos
                en vertederos.
              </Text>
            </Box>

            <Box
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              p={5}
            >
              <Text fontFamily="heading" fontWeight="600" mb={4}>
                Ofertas Recibidas
              </Text>
              <VStack
                border="1px dashed"
                borderColor="gray.300"
                borderRadius="lg"
                py={8}
                px={4}
                gap={2}
                textAlign="center"
              >
                <Icono componente={<MdOutlineInbox />} tamanio="3xl" color="gray.400" />
                <Text fontWeight="600">Aún no hay ofertas</Text>
                <Text fontSize="sm" color="gray.600">
                  Los recolectores de tu zona han sido notificados. Las ofertas aparecerán aquí.
                </Text>
              </VStack>
              <Button
                mt={4}
                w="100%"
                variant="outline"
                colorPalette="verde"
                rounded="lg"
                onClick={() => navigate(`${prefijoRuta}/ver-ofertas`)}
              >
                Ver Ofertas
              </Button>
            </Box>
          </VStack>
        </SimpleGrid>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionesDisponibles;
