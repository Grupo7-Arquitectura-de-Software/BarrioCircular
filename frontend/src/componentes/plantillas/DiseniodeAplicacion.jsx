import { useState } from "react";
import { Box, CloseButton, Drawer, Flex, Portal } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import BarraLateralNavegacion from "../organismos/BarraLateralNavegacion.jsx";
import BarraSuperiorApp from "../organismos/BarraSuperiorApp.jsx";

/**
 * Plantilla del panel de la aplicación: barra lateral de navegación,
 * barra superior con buscador y área de contenido.
 * `navegacion` y `rutaNuevaPublicacion` configuran la barra lateral por rol.
 * En pantallas pequeñas la barra lateral se abre como cajón desde el botón
 * hamburguesa de la barra superior.
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
  const [menuAbierto, setMenuAbierto] = useState(false);

  return (
    <Flex h="100vh" w="100%" bg="fondo.pagina" align="stretch" overflow="hidden">
      <BarraLateralNavegacion
        elementos={navegacion}
        subtitulo={subtituloMarca}
        rutaNuevaPublicacion={rutaNuevaPublicacion}
      />

      {/* Navegación móvil: la misma barra lateral dentro de un cajón. */}
      <Drawer.Root
        open={menuAbierto}
        onOpenChange={(evento) => setMenuAbierto(evento.open)}
        placement="start"
      >
        <Portal>
          <Drawer.Backdrop />
          <Drawer.Positioner>
            <Drawer.Content maxW="280px">
              <Drawer.Body p={0}>
                <BarraLateralNavegacion
                  elementos={navegacion}
                  subtitulo={subtituloMarca}
                  rutaNuevaPublicacion={rutaNuevaPublicacion}
                  variante="cajon"
                  alNavegar={() => setMenuAbierto(false)}
                />
              </Drawer.Body>
              <Drawer.CloseTrigger asChild>
                <CloseButton size="sm" position="absolute" top={3} right={3} />
              </Drawer.CloseTrigger>
            </Drawer.Content>
          </Drawer.Positioner>
        </Portal>
      </Drawer.Root>

      <Flex direction="column" flex="1" minW={0}>
        <BarraSuperiorApp
          etiquetaVolver={etiquetaVolver}
          alVolver={manejarVolver}
          mostrarBuscador={mostrarBuscador}
          alAbrirMenu={() => setMenuAbierto(true)}
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
