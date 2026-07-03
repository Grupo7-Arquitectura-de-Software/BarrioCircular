import { Box, Button, Flex, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import PanelResultadoFinal from "../componentes/organismos/PanelResultadoFinal";

/**
 * Resultado final del comprador (mockup "Resultado Final - Centro de
 * Recolección"): confirmación con resumen del material recibido.
 */
const PaginaResultadoFinalComprador = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const rutaInicio = rol === "centro" ? "/centro/buscar-materiales" : "/recolector/inicio";

  return (
    <Flex minH="100vh" bg="fondo.pagina" align="center" justify="center" p={{ base: 4, md: 8 }}>
      <Box
        w="100%"
        maxW="440px"
        bg="fondo.tarjeta"
        border="1px solid"
        borderColor="gray.200"
        borderRadius="xl"
        boxShadow="md"
        p={{ base: 6, md: 8 }}
      >
        <PanelResultadoFinal
          tituloResumen="Resumen de material recibido"
          tipoMaterial="Cartón"
          pesoKg={250}
          monto="$25.00"
        />

        <VStack gap={3} mt={8}>
          <Button
            w="100%"
            colorPalette="verde"
            bg="marca.primario"
            rounded="lg"
            onClick={() => navigate(rutaInicio)}
          >
            Finalizar
          </Button>
          <Button
            w="100%"
            variant="outline"
            colorPalette="verde"
            rounded="lg"
            onClick={() => navigate(rutaInicio)}
          >
            Volver al Inicio
          </Button>
        </VStack>
      </Box>
    </Flex>
  );
};

export default PaginaResultadoFinalComprador;
