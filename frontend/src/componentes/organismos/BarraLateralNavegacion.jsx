import { Box, Button, Circle, Flex, HStack, Text, VStack } from "@chakra-ui/react";
import { useLocation, useNavigate } from "react-router-dom";
import { LuLeaf } from "react-icons/lu";
import { MdAdd, MdOutlineHelpOutline, MdOutlineLogout } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";
import { useCerrarSesion } from "@/utilidades/useCerrarSesion";

const ElementoNavegacion = ({ etiqueta, icono, activo, alHacerClick, deshabilitado }) => (
  <HStack
    as="button"
    type="button"
    onClick={alHacerClick}
    disabled={deshabilitado}
    w="100%"
    px={3}
    py={2}
    gap={3}
    borderRadius="lg"
    bg={activo ? "marca.primario" : "transparent"}
    color={activo ? "white" : "gray.700"}
    fontWeight={activo ? "600" : "500"}
    fontSize="sm"
    cursor={deshabilitado ? "not-allowed" : "pointer"}
    opacity={deshabilitado ? 0.55 : 1}
    _hover={!activo && !deshabilitado ? { bg: "fondo.cabeceraTarjeta" } : undefined}
    transition="background 0.15s ease"
  >
    <Icono componente={icono} tamanio="lg" />
    <Text>{etiqueta}</Text>
  </HStack>
);

/**
 * Barra lateral de navegación del panel (mockups Entregables 3-5).
 * `elementos`: [{ etiqueta, icono, ruta }] — sin `ruta` se muestra deshabilitado.
 */
const BarraLateralNavegacion = ({
  elementos = [],
  subtitulo = "Centro de Economía Circular",
  rutaNuevaPublicacion,
}) => {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const cerrarSesion = useCerrarSesion();

  // La ruta de ayuda vive bajo el prefijo del rol (/ciudadano, /recolector, /centro),
  // que se deriva del primer elemento de navegación con ruta.
  const prefijoRol = elementos.find((elemento) => elemento.ruta)?.ruta.split("/")[1];
  const rutaAyuda = prefijoRol ? `/${prefijoRol}/ayuda` : null;

  return (
    <Flex
      as="aside"
      direction="column"
      w="256px"
      flexShrink={0}
      h="100%"
      overflowY="auto"
      bg="fondo.pagina"
      borderRight="1px solid"
      borderColor="gray.200"
      px={4}
      py={5}
      display={{ base: "none", lg: "flex" }}
    >
      {/* Marca */}
      <HStack gap={2} px={2} mb={8} align="center">
        <Circle size="36px" bg="fondo.tarjeta" border="1px solid" borderColor="gray.200">
          <Icono componente={<LuLeaf />} tamanio="lg" color="marca.primario" />
        </Circle>
        <Box>
          <Text fontFamily="heading" fontWeight="700" fontSize="lg" color="marca.primario">
            BarrioCircular
          </Text>
          <Text fontSize="xs" color="gray.600">
            {subtitulo}
          </Text>
        </Box>
      </HStack>

      {/* Navegación */}
      <VStack gap={1} align="stretch">
        {elementos.map((elemento) => (
          <ElementoNavegacion
            key={elemento.etiqueta}
            etiqueta={elemento.etiqueta}
            icono={elemento.icono}
            activo={Boolean(elemento.ruta && pathname.startsWith(elemento.ruta))}
            deshabilitado={!elemento.ruta}
            alHacerClick={() => elemento.ruta && navigate(elemento.ruta)}
          />
        ))}
      </VStack>

      <Box flex="1" />

      {/* Acciones inferiores */}
      <VStack gap={2} align="stretch" borderTop="1px solid" borderColor="gray.200" pt={4}>
        <ElementoNavegacion
          etiqueta="Ayuda"
          icono={<MdOutlineHelpOutline />}
          activo={Boolean(rutaAyuda && pathname.startsWith(rutaAyuda))}
          deshabilitado={!rutaAyuda}
          alHacerClick={() => rutaAyuda && navigate(rutaAyuda)}
        />
        <ElementoNavegacion
          etiqueta="Cerrar Sesión"
          icono={<MdOutlineLogout />}
          alHacerClick={cerrarSesion}
        />
        {rutaNuevaPublicacion && (
          <Button
            colorPalette="verde"
            bg="marca.primario"
            rounded="lg"
            mt={2}
            onClick={() => navigate(rutaNuevaPublicacion)}
          >
            <MdAdd /> Nueva Publicación
          </Button>
        )}
      </VStack>
    </Flex>
  );
};

export default BarraLateralNavegacion;
