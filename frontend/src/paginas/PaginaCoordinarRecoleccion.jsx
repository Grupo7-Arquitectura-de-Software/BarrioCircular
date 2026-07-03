import { Box, Flex, HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { MdArrowBack, MdOutlineLocationOn } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import CalendarioCoordinacion from "../componentes/organismos/CalendarioCoordinacion";
import PanelDetalleOperacion from "../componentes/moleculas/PanelDetalleOperacion.jsx";
import TarjetaVendedor from "../componentes/moleculas/TarjetaVendedor.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { NAVEGACION_CENTRO, SUBTITULO_CENTRO } from "@/utilidades/navegacionPanel";

/**
 * Coordinar logística del intercambio (mockup Entregable 4): calendario,
 * hora y modalidad con el detalle de la operación al costado.
 */
const PaginaCoordinarRecoleccion = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const esCentro = rol === "centro";

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : []}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : undefined}
      mostrarBuscador={false}
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <HStack gap={3}>
            <Icono
              componente={
                <MdArrowBack cursor="pointer" onClick={() => navigate(-1)} aria-label="Volver" />
              }
              tamanio="xl"
            />
            <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
              Coordinar Logística
            </Text>
          </HStack>
          <Text color="gray.600" pl={9}>
            Selecciona fecha, hora y modalidad para el intercambio.
          </Text>
        </VStack>

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          <Box flex="1" w="100%">
            <CalendarioCoordinacion
              alConfirmarCita={() => navigate(`/${rol}/verificar/${id ?? "1"}`)}
            />
          </Box>

          <VStack align="stretch" gap={5} w={{ base: "100%", lg: "340px" }} flexShrink={0}>
            <PanelDetalleOperacion
              insignia="Oferta Aceptada"
              material="Cartón Corrugado"
              filas={[{ etiqueta: "Peso Estimado", valor: "250 kg" }]}
              monto="$25.00"
            />
            <TarjetaVendedor
              rotulo="VENDEDOR (RECICLADOR)"
              nombre="Juan P."
              calificacion="4.8"
              detalleCalificacion="(120 operaciones)"
            />
            <Box
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              p={5}
            >
              <HStack gap={2} mb={1}>
                <Icono componente={<MdOutlineLocationOn />} tamanio="lg" color="marca.secundario" />
                <Text fontWeight="700" fontSize="sm">
                  Ubicación del Material
                </Text>
              </HStack>
              <Text fontSize="sm" color="gray.600" mb={3}>
                Av. América y Naciones Unidas, Quito
              </Text>
              {/* TODO: integrar mapa cuando esté disponible. */}
              <Flex
                h="110px"
                bg="fondo.cabeceraTarjeta"
                borderRadius="lg"
                align="center"
                justify="center"
                color="gray.500"
                fontSize="sm"
              >
                Mapa del sector
              </Flex>
            </Box>
          </VStack>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCoordinarRecoleccion;
