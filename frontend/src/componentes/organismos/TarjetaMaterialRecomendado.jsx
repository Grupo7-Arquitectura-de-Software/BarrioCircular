import { Badge, Box, Button, Circle, Flex, HStack, Image, Text } from "@chakra-ui/react";
import { MdOutlineImage, MdOutlineLocationOn, MdOutlineScale, MdVerified } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";
import BotonWhatsApp from "../atomos/BotonWhatsApp.jsx";

/**
 * Tarjeta horizontal de material recomendado (mockup "Abastecimiento de
 * Materiales"): imagen, título con precio/kg, chips, descripción y reserva.
 * `distanciaKm` y `puntuacion` son opcionales (dependen del emparejamiento).
 */
const TarjetaMaterialRecomendado = ({
  titulo,
  precioPorKilo,
  pesoKg,
  ubicacion,
  distanciaKm,
  descripcion,
  puntuacion,
  imagenUrl,
  verificado = true,
  telefonoCreador,
  alVerDetalle,
  alReservar,
  reservando = false,
}) => {
  const acentoPuntuacion = puntuacion >= 90 ? "marca.primario" : "marca.secundario";

  return (
    <Flex
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
      direction={{ base: "column", md: "row" }}
      cursor={alVerDetalle ? "pointer" : "default"}
      onClick={alVerDetalle}
      transition="box-shadow 0.15s ease"
      _hover={alVerDetalle ? { boxShadow: "md" } : undefined}
    >
      {/* Imagen */}
      <Box
        position="relative"
        w={{ base: "100%", md: "220px" }}
        minH="170px"
        bg="fondo.pagina"
        flexShrink={0}
      >
        {imagenUrl ? (
          <Image src={imagenUrl} alt={titulo} w="100%" h="100%" fit="cover" />
        ) : (
          <Flex h="100%" align="center" justify="center" color="gray.300">
            <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
          </Flex>
        )}
        {verificado && (
          <Badge
            position="absolute"
            top={3}
            left={3}
            bg="verde.100"
            color="marca.primario"
            borderRadius="full"
            px={2}
            py={1}
          >
            <MdVerified /> Verificado
          </Badge>
        )}
      </Box>

      {/* Contenido */}
      <Flex direction="column" flex="1" p={5} gap={2}>
        <Flex justify="space-between" gap={3} align="flex-start">
          <Text fontFamily="heading" fontWeight="600" fontSize="xl">
            {titulo}
          </Text>
          <Text
            fontFamily="heading"
            fontWeight="700"
            fontSize="xl"
            color="marca.primario"
            flexShrink={0}
          >
            {precioPorKilo}/kg
          </Text>
        </Flex>

        <HStack gap={2}>
          <Badge
            bg="fondo.pagina"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="md"
            px={2}
            py={1}
          >
            <MdOutlineScale /> {pesoKg} kg
          </Badge>
          <Badge
            bg="fondo.pagina"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="md"
            px={2}
            py={1}
          >
            <MdOutlineLocationOn /> {ubicacion}
            {distanciaKm !== undefined && ` - ${distanciaKm}km`}
          </Badge>
          {telefonoCreador && (
            <Box ml={2}>
              <BotonWhatsApp telefono={telefonoCreador} tamanio="sm" />
            </Box>
          )}
        </HStack>

        <Text fontSize="sm" color="gray.600" flex="1">
          {descripcion}
        </Text>

        <Flex
          justify="space-between"
          align="center"
          borderTop="1px solid"
          borderColor="gray.100"
          pt={3}
          mt={1}
        >
          {puntuacion !== undefined ? (
            <HStack gap={2}>
              <Circle
                size="34px"
                border="2px solid"
                borderColor={acentoPuntuacion}
                color={acentoPuntuacion}
              >
                <Text fontSize="sm" fontWeight="700">
                  {puntuacion}
                </Text>
              </Circle>
              <Text fontSize="sm" color="gray.600">
                Puntuación de Conveniencia
              </Text>
            </HStack>
          ) : (
            <Box />
          )}
          {alReservar && (
            <Button
              size="sm"
              colorPalette="verde"
              bg="marca.primario"
              rounded="lg"
              loading={reservando}
              loadingText="Reservando"
              onClick={(evento) => {
                evento.stopPropagation();
                alReservar();
              }}
            >
              Reservar
            </Button>
          )}
        </Flex>
      </Flex>
    </Flex>
  );
};

export default TarjetaMaterialRecomendado;
