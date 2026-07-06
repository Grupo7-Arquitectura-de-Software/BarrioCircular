import { Box, Button, Flex, HStack, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineEventAvailable, MdOutlineInbox } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { NAVEGACION_CENTRO, SUBTITULO_CENTRO } from "@/utilidades/navegacionPanel";
import { barrioMasCercano, etiquetaTipoResiduo } from "@/utilidades/barriosQuito";
import {
  obtenerMisReservas,
  obtenerPublicacionesDisponibles,
} from "@/servicios/publicacionService";
import { usePublicaciones } from "@/utilidades/usePublicaciones";
import { useReservarPublicacion } from "@/utilidades/useReservarPublicacion";

const TarjetaReservaActiva = ({ reserva, alCoordinar }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="lg" p={4}>
    <Flex justify="space-between" gap={2} mb={1}>
      <Text fontWeight="600" fontSize="sm">
        {reserva.pesoKg}kg de {etiquetaTipoResiduo(reserva.tipoResiduo)}
      </Text>
      <Text fontWeight="700" fontSize="sm" color="marca.primario">
        ${Number(reserva.precioPorKilo).toFixed(2)}/kg
      </Text>
    </Flex>
    <Text fontSize="xs" color="gray.600" mb={2}>
      {barrioMasCercano(reserva.latitud, reserva.longitud)}
    </Text>
    <Button size="xs" variant="outline" colorPalette="verde" rounded="lg" onClick={alCoordinar}>
      Coordinar recolección
    </Button>
  </Box>
);

/**
 * Buscador de materiales del Centro de Recolección: publicaciones disponibles
 * reales con reserva directa y panel con las reservas activas del centro.
 */
const PaginaCentroBuscarMateriales = () => {
  const navigate = useNavigate();
  const { publicaciones, cargando, mensajeError } = usePublicaciones(
    obtenerPublicacionesDisponibles,
  );
  const { publicaciones: reservas, cargando: cargandoReservas } =
    usePublicaciones(obtenerMisReservas);
  const { reservar, reservandoId } = useReservarPublicacion("centro");

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
            Descubre y reserva materiales reciclables publicados por la comunidad.
          </Text>
        </VStack>

        <FormularioBuscarMateriales />

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          {/* Publicaciones disponibles */}
          <VStack align="stretch" gap={4} flex="1" minW={0} w="100%">
            <Text fontFamily="heading" fontWeight="700" fontSize="xl">
              Materiales Disponibles
            </Text>

            {cargando ? (
              <Flex justify="center" py={12}>
                <Spinner size="lg" color="marca.primario" />
              </Flex>
            ) : mensajeError ? (
              <Text color="marca.error">{mensajeError}</Text>
            ) : publicaciones.length === 0 ? (
              <VStack
                border="1px dashed"
                borderColor="gray.300"
                borderRadius="xl"
                py={12}
                gap={3}
                textAlign="center"
              >
                <Icono componente={<MdOutlineInbox />} tamanio="4xl" color="gray.400" />
                <Text fontWeight="600">No hay materiales disponibles por ahora</Text>
              </VStack>
            ) : (
              publicaciones.map((publicacion) => (
                <TarjetaMaterialRecomendado
                  key={publicacion.publicacionId}
                  titulo={`${publicacion.pesoKg}kg de ${etiquetaTipoResiduo(publicacion.tipoResiduo)}`}
                  precioPorKilo={`$${Number(publicacion.precioPorKilo).toFixed(2)}`}
                  pesoKg={publicacion.pesoKg}
                  ubicacion={barrioMasCercano(publicacion.latitud, publicacion.longitud)}
                  descripcion={`Publicado el ${new Date(publicacion.fechaCreacion).toLocaleDateString()}`}
                  imagenUrl={publicacion.evidenciaUrl}
                  alVerDetalle={() => navigate(`/centro/detalle/${publicacion.publicacionId}`)}
                  alReservar={() => reservar(publicacion.publicacionId)}
                  reservando={reservandoId === publicacion.publicacionId}
                />
              ))
            )}
          </VStack>

          {/* Reservas activas del centro */}
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
              <Icono
                componente={<MdOutlineEventAvailable />}
                tamanio="lg"
                color="marca.secundario"
              />
              <Text fontFamily="heading" fontWeight="700">
                Reservas Activas
              </Text>
            </HStack>
            {cargandoReservas ? (
              <Flex justify="center" py={6}>
                <Spinner color="marca.primario" />
              </Flex>
            ) : reservas.length === 0 ? (
              <VStack
                border="1px dashed"
                borderColor="gray.300"
                borderRadius="lg"
                py={8}
                px={3}
                gap={2}
                textAlign="center"
                bg="fondo.tarjeta"
              >
                <Icono componente={<MdOutlineInbox />} tamanio="2xl" color="gray.400" />
                <Text fontSize="sm" fontWeight="600">
                  Aún no tienes reservas
                </Text>
                <Text fontSize="xs" color="gray.600">
                  Cuando reserves un material aparecerá aquí para coordinar su recolección.
                </Text>
              </VStack>
            ) : (
              <VStack align="stretch" gap={3}>
                {reservas.map((reserva) => (
                  <TarjetaReservaActiva
                    key={reserva.publicacionId}
                    reserva={reserva}
                    alCoordinar={() => navigate(`/centro/coordinar/${reserva.publicacionId}`)}
                  />
                ))}
              </VStack>
            )}
            <Button
              mt={4}
              w="100%"
              variant="outline"
              colorPalette="azul"
              bg="fondo.tarjeta"
              rounded="lg"
              onClick={() => navigate("/centro/publicaciones-recomendadas")}
            >
              Ver Todas las Publicaciones
            </Button>
          </Box>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCentroBuscarMateriales;
