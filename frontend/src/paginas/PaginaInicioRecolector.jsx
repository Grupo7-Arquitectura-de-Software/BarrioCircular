import { Box, Flex, Text, VStack } from "@chakra-ui/react";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelRutaRecoleccion from "../componentes/organismos/PanelRutaRecoleccion.jsx";
import PanelCoordinacionMensajes from "../componentes/organismos/PanelCoordinacionMensajes.jsx";
import { NAVEGACION_RECOLECTOR, SUBTITULO_RECOLECTOR } from "@/utilidades/navegacionPanel";

/**
 * Inicio del reciclador híbrido (mockup "Gestión Híbrida"): resumen
 * operativo con ruta de recolección y mensajes de coordinación.
 */
const PaginaInicioRecolector = () => {
  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_RECOLECTOR}
      subtituloMarca={SUBTITULO_RECOLECTOR}
      rutaNuevaPublicacion="/recolector/vender/crear-publicacion"
      anchoContenido="1160px"
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Resumen Operativo
          </Text>
          <Text color="gray.600">
            Gestiona tus flujos de materiales y el impacto en la comunidad.
          </Text>
        </VStack>

        <Flex gap={6} align="stretch" direction={{ base: "column", lg: "row" }}>
          <Box flex="2" minW={0}>
            <PanelRutaRecoleccion alVerRuta={() => {}} />
          </Box>
          <Box flex="1" minW={{ lg: "320px" }}>
            <PanelCoordinacionMensajes alVerTodos={() => {}} />
          </Box>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaInicioRecolector;
