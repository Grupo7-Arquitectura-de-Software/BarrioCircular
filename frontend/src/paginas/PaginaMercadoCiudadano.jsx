import { Box, HStack, Text, VStack } from "@chakra-ui/react";
import { MdOutlineInfo } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";
import { MATERIALES_RECOMENDADOS } from "@/utilidades/datosMercado";

/**
 * Mercado comunitario del ciudadano: explora las publicaciones activas de la
 * comunidad como referencia de precios y demanda de materiales.
 */
const PaginaMercadoCiudadano = () => {
  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CIUDADANO}
      rutaNuevaPublicacion={RUTA_NUEVA_PUBLICACION_CIUDADANO}
      anchoContenido="960px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text
            fontFamily="heading"
            fontWeight="700"
            fontSize={{ base: "2xl", md: "3xl" }}
            color="marca.primario"
          >
            Mercado
          </Text>
          <Text color="gray.600">
            Explora los materiales que publica la comunidad y sus precios de referencia.
          </Text>
        </VStack>

        <FormularioBuscarMateriales />

        <HStack
          bg="fondo.cabeceraTarjeta"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="lg"
          px={4}
          py={3}
          gap={2}
        >
          <Icono componente={<MdOutlineInfo />} tamanio="lg" color="marca.secundario" />
          <Text fontSize="sm" color="gray.700">
            Usa estos precios como referencia para tus propias publicaciones. Los recicladores y
            centros de acopio ofertan sobre publicaciones como estas.
          </Text>
        </HStack>

        <VStack align="stretch" gap={4}>
          {MATERIALES_RECOMENDADOS.map((material) => (
            <TarjetaMaterialRecomendado
              key={material.id}
              titulo={material.titulo}
              precioPorKilo={material.precioPorKilo}
              pesoKg={material.pesoKg}
              ubicacion={material.ubicacion}
              distanciaKm={material.distanciaKm}
              descripcion={material.descripcion}
              puntuacion={material.puntuacion}
            />
          ))}
        </VStack>

        <Box />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaMercadoCiudadano;
