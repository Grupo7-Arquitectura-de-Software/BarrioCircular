import { Box, Button, Flex, HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdCheckCircleOutline, MdOutlineGavel, MdOutlineSchedule } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { NAVEGACION_CENTRO, SUBTITULO_CENTRO } from "@/utilidades/navegacionPanel";
import { MATERIALES_RECOMENDADOS } from "@/utilidades/datosMercado";

// Ofertas realizadas por el centro; el vendedor decide si las acepta o rechaza.
const OFERTAS_ACTIVAS = [
  {
    id: 1,
    titulo: "Casco de Vidrio Mixto",
    precioPorKilo: "$0.12",
    detalle: "200kg • Enviada hace 2 horas",
    estado: "ACEPTADA",
    estadoTexto: "Oferta aceptada",
  },
  {
    id: 2,
    titulo: "Jarras de Leche HDPE",
    precioPorKilo: "$0.55",
    detalle: "80kg • Enviada hace 45 mins",
    estado: "PENDIENTE",
    estadoTexto: "Esperando respuesta del vendedor",
  },
];

const TarjetaOfertaActiva = ({ oferta }) => {
  const aceptada = oferta.estado === "ACEPTADA";
  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="lg" p={4}>
      <Flex justify="space-between" gap={2} mb={1}>
        <Text fontWeight="600" fontSize="sm">
          {oferta.titulo}
        </Text>
        <Text fontWeight="700" fontSize="sm" color="marca.primario">
          {oferta.precioPorKilo}/kg
        </Text>
      </Flex>
      <Text fontSize="xs" color="gray.600" mb={2}>
        {oferta.detalle}
      </Text>
      <HStack gap={1} color={aceptada ? "marca.primario" : "gray.600"} fontSize="xs">
        <Icono
          componente={aceptada ? <MdCheckCircleOutline /> : <MdOutlineSchedule />}
          tamanio="sm"
        />
        <Text fontWeight="600">{oferta.estadoTexto}</Text>
      </HStack>
    </Box>
  );
};

/**
 * Buscador de materiales del Centro de Recolección (mockup "Abastecimiento
 * de Materiales"): filtros, recomendaciones y panel de ofertas activas.
 */
const PaginaCentroBuscarMateriales = () => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CENTRO}
      subtituloMarca={SUBTITULO_CENTRO}
      anchoContenido="1160px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text
            fontFamily="heading"
            fontWeight="700"
            fontSize={{ base: "2xl", md: "3xl" }}
            color="marca.primario"
          >
            Abastecimiento de Materiales
          </Text>
          <Text color="gray.600">
            Descubre y oferta por materiales reciclables de alta calidad verificados por la
            comunidad.
          </Text>
        </VStack>

        <FormularioBuscarMateriales />

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          {/* Recomendaciones */}
          <VStack align="stretch" gap={4} flex="1" minW={0} w="100%">
            <Flex justify="space-between" align="center">
              <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                Ofertas Recomendadas
              </Text>
              <Text fontSize="sm" color="gray.600">
                Ordenar por:{" "}
                <Text as="span" color="marca.primario" fontWeight="600">
                  Conveniencia
                </Text>
              </Text>
            </Flex>

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
                alVerDetalle={() => navigate(`/centro/detalle/${material.id}`)}
                alHacerOferta={() => navigate(`/centro/realizar-oferta/${material.id}`)}
              />
            ))}
          </VStack>

          {/* Ofertas activas */}
          <Box
            w={{ base: "100%", lg: "300px" }}
            flexShrink={0}
            bg="fondo.cabeceraTarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            p={4}
          >
            <HStack gap={2} mb={4}>
              <Icono componente={<MdOutlineGavel />} tamanio="lg" color="marca.secundario" />
              <Text fontFamily="heading" fontWeight="700">
                Ofertas Activas
              </Text>
            </HStack>
            <VStack align="stretch" gap={3}>
              {OFERTAS_ACTIVAS.map((oferta) => (
                <TarjetaOfertaActiva key={oferta.id} oferta={oferta} />
              ))}
            </VStack>
            <Button
              mt={4}
              w="100%"
              variant="outline"
              colorPalette="azul"
              bg="fondo.tarjeta"
              rounded="lg"
              onClick={() => navigate("/centro/ofertas-recomendadas")}
            >
              Ver Todas las Ofertas
            </Button>
          </Box>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCentroBuscarMateriales;
