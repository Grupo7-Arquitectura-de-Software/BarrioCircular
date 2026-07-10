import { Box, Button, Flex, Text, VStack } from "@chakra-ui/react";
import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { MdOutlineQrCode2 } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelRutaRecoleccion from "../componentes/organismos/PanelRutaRecoleccion.jsx";
import PanelCoordinacionMensajes from "../componentes/organismos/PanelCoordinacionMensajes.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { NAVEGACION_RECOLECTOR, SUBTITULO_RECOLECTOR } from "@/utilidades/navegacionPanel";
import { useRutaRecoleccion } from "@/utilidades/useRutaRecoleccion";

/**
 * Inicio del reciclador híbrido (mockup "Gestión Híbrida"): resumen
 * operativo con ruta de recolección y mensajes de coordinación.
 */
const PaginaInicioRecolector = () => {
  const navigate = useNavigate();
  const { ruta, cargando, mensajeError, cargarRutaActiva } = useRutaRecoleccion();
  const cargaInicialRef = useRef(false);

  useEffect(() => {
    if (cargaInicialRef.current) return;
    cargaInicialRef.current = true;
    cargarRutaActiva();
  }, [cargarRutaActiva]);

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

        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
          <Flex
            justify="space-between"
            align={{ base: "stretch", md: "center" }}
            gap={4}
            direction={{ base: "column", md: "row" }}
          >
            <Flex gap={4} align="flex-start">
              <Box
                bg="fondo.cabeceraTarjeta"
                borderRadius="lg"
                color="marca.primario"
                p={3}
                flexShrink={0}
              >
                <Icono componente={<MdOutlineQrCode2 />} tamanio="2xl" />
              </Box>
              <VStack align="stretch" gap={1}>
                <Text fontFamily="heading" fontWeight="700" fontSize="lg">
                  Credencial de confianza
                </Text>
                <Text color="gray.600" fontSize="sm">
                  Muestra tu QR de identidad cuando vayas a recoger materiales para que el ciudadano
                  confirme que perteneces a Barrio Circular.
                </Text>
              </VStack>
            </Flex>
            <Button
              colorPalette="verde"
              bg="marca.primario"
              rounded="lg"
              onClick={() => navigate("/recolector/identidad")}
            >
              Ver mi QR
            </Button>
          </Flex>
        </Box>

        <Flex gap={6} align="stretch" direction={{ base: "column", lg: "row" }}>
          <Box flex="2" minW={0}>
            <PanelRutaRecoleccion
              ruta={ruta}
              cargando={cargando}
              mensajeError={mensajeError}
              alVerRuta={() => navigate("/recolector/ruta-recoleccion")}
            />
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
