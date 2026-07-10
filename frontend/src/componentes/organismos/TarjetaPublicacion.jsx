import {
  Badge,
  Box,
  Button,
  Flex,
  HStack,
  IconButton,
  Image,
  Text,
  VStack,
} from "@chakra-ui/react";
import {
  MdOutlineDelete,
  MdOutlineEdit,
  MdOutlineImage,
  MdOutlineLocationOn,
  MdOutlineScale,
} from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const ESTILOS_ESTADO = {
  Disponible: { bg: "marca.primario", color: "white" },
  "Recolección Pendiente": { bg: "fondo.tarjeta", color: "gray.700" },
  "Acción Requerida": { bg: "red.50", color: "marca.error" },
  Reservado: { bg: "azul.50", color: "marca.secundario" },
  Completada: { bg: "verde.50", color: "marca.primario" },
  Cancelada: { bg: "red.50", color: "marca.error" },
};

/**
 * Tarjeta de publicación del panel (mockup "Mis Publicaciones"):
 * imagen con insignia de estado, título, descripción y pie con peso/ubicación.
 */
const TarjetaPublicacion = ({
  titulo = "Publicación",
  descripcion = "",
  pesoKg,
  ubicacion,
  estado = "Disponible",
  imagenUrl,
  etiquetaAccion,
  alAccionar,
  accionando,
  alEditar,
  alEliminar,
  eliminando,
  alHacerClick,
}) => {
  const estiloEstado = ESTILOS_ESTADO[estado] || ESTILOS_ESTADO.Disponible;

  return (
    <Flex
      direction="column"
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
      cursor={alHacerClick ? "pointer" : "default"}
      onClick={alHacerClick}
      transition="box-shadow 0.15s ease"
      _hover={alHacerClick ? { boxShadow: "md" } : undefined}
    >
      {/* Imagen con insignia de estado */}
      <Box position="relative" h="180px" bg="fondo.pagina">
        {imagenUrl ? (
          <Image src={imagenUrl} alt={titulo} w="100%" h="100%" fit="cover" />
        ) : (
          <Flex h="100%" align="center" justify="center" color="gray.300">
            <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
          </Flex>
        )}
        <Badge
          position="absolute"
          top={3}
          right={3}
          bg={estiloEstado.bg}
          color={estiloEstado.color}
          borderRadius="full"
          px={3}
          py={1}
          boxShadow="sm"
        >
          ● {estado}
        </Badge>
      </Box>

      {/* Contenido */}
      <VStack align="stretch" gap={2} p={4} flex="1">
        <Text fontFamily="heading" fontWeight="600" fontSize="lg">
          {titulo}
        </Text>
        {descripcion && (
          <Text fontSize="sm" color="gray.600" flex="1">
            {descripcion}
          </Text>
        )}

        <HStack
          justify="space-between"
          borderTop="1px solid"
          borderColor="gray.100"
          pt={3}
          mt={2}
          color="gray.700"
          fontSize="sm"
        >
          <HStack gap={1}>
            <Icono componente={<MdOutlineScale />} tamanio="md" />
            <Text fontWeight="600">{pesoKg} kg</Text>
          </HStack>
          <HStack gap={1}>
            <Icono componente={<MdOutlineLocationOn />} tamanio="md" />
            <Text>{ubicacion}</Text>
          </HStack>
        </HStack>

        {etiquetaAccion && (
          <Button
            variant="outline"
            colorPalette="azul"
            rounded="lg"
            size="sm"
            loading={accionando}
            onClick={(evento) => {
              evento.stopPropagation();
              alAccionar?.();
            }}
          >
            {etiquetaAccion}
          </Button>
        )}

        {(alEditar || alEliminar) && (
          <HStack gap={2}>
            {alEditar && (
              <Button
                variant="outline"
                colorPalette="azul"
                rounded="lg"
                size="sm"
                flex="1"
                onClick={(evento) => {
                  evento.stopPropagation();
                  alEditar();
                }}
              >
                <MdOutlineEdit /> Editar
              </Button>
            )}
            {alEliminar && (
              <IconButton
                aria-label="Eliminar publicación"
                variant="outline"
                colorPalette="red"
                rounded="lg"
                size="sm"
                loading={eliminando}
                onClick={(evento) => {
                  evento.stopPropagation();
                  alEliminar();
                }}
              >
                <MdOutlineDelete />
              </IconButton>
            )}
          </HStack>
        )}
      </VStack>
    </Flex>
  );
};

export default TarjetaPublicacion;
