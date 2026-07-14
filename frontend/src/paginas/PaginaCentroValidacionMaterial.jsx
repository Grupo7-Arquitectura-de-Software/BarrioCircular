import { Box, Flex, HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion";
import FormulariodeVerificacion from "@/componentes/organismos/FormulariodeVerificacion";
import PanelDetalleOperacion from "@/componentes/moleculas/PanelDetalleOperacion.jsx";
import TarjetaVendedor from "@/componentes/moleculas/TarjetaVendedor.jsx";
import Icono from "@/componentes/atomos/Icono.jsx";
import { NAVEGACION_CENTRO, SUBTITULO_CENTRO } from "@/utilidades/navegacionPanel";
import { limpiarEtapaReserva } from "@/utilidades/progresoReserva";

/**
 * Confirmación de recepción del material (mockup "Confirmar Recepción").
 */
const PaginaCentroValidacionMaterial = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  // La operación termina: se descarta la etapa guardada de la reserva.
  const confirmarRecepcion = () => {
    limpiarEtapaReserva(id);
    navigate("/centro/resultado");
  };

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CENTRO}
      subtituloMarca={SUBTITULO_CENTRO}
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
              Confirmar Recepción
            </Text>
          </HStack>
          <Text color="gray.600" pl={9}>
            Verifica y registra los detalles finales del material recibido.
          </Text>
        </VStack>

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          <Box flex="1" w="100%">
            <FormulariodeVerificacion pesoEstimadoKg={250} alConfirmar={confirmarRecepcion} />
          </Box>

          <VStack align="stretch" gap={5} w={{ base: "100%", lg: "340px" }} flexShrink={0}>
            <PanelDetalleOperacion
              insignia="Operación Activa"
              material="Cartón Corrugado"
              filas={[{ etiqueta: "ID Operación", valor: "#OP-9923", esInsignia: true }]}
              monto="$25.00"
              etiquetaMonto="Monto Acordado (Est.)"
            />
            <TarjetaVendedor
              rotulo="VENDEDOR (RECICLADOR)"
              nombre="Juan P."
              calificacion="4.8"
              detalleCalificacion="(120 operaciones)"
            />
          </VStack>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCentroValidacionMaterial;
