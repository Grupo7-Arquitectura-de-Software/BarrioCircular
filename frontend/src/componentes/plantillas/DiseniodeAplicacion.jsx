import { Box, Flex } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import BarraLateralNavegacion from "../organismos/BarraLateralNavegacion.jsx";
import BarraSuperiorApp from "../organismos/BarraSuperiorApp.jsx";

/**
 * Plantilla del panel de la aplicación: barra lateral de navegación,
 * barra superior con buscador y área de contenido.
 * `navegacion` y `rutaNuevaPublicacion` configuran la barra lateral por rol.
 */
const DiseniodeAplicacion = ({
  children,
  navegacion = [],
  subtituloMarca,
  rutaNuevaPublicacion,
  etiquetaVolver,
  alVolver,
  mostrarAtras = false,
  mostrarBuscador = true,
  anchoContenido = "1080px",
}) => {
  const navigate = useNavigate();
  const manejarVolver = alVolver || (mostrarAtras ? () => navigate(-1) : undefined);

  return (
    <Flex minH="100vh" w="100%" bg="fondo.pagina" align="stretch">
      <BarraLateralNavegacion
        elementos={navegacion}
        subtitulo={subtituloMarca}
        rutaNuevaPublicacion={rutaNuevaPublicacion}
      />
      <Flex direction="column" flex="1" minW={0}>
        <BarraSuperiorApp
          etiquetaVolver={etiquetaVolver}
          alVolver={manejarVolver}
          mostrarBuscador={mostrarBuscador}
        />
        <Box flex="1" overflowY="auto" px={{ base: 4, md: 8 }} py={{ base: 5, md: 8 }}>
          <Box maxW={anchoContenido} mx="auto">
            {children}
          </Box>
        </Box>
      </Flex>
    </Flex>
  );
};

export default DiseniodeAplicacion;
