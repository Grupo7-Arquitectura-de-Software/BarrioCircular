import { Box, Button, Flex, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import PanelResultadoFinal from "../componentes/organismos/PanelResultadoFinal";

/**
 * Confirmación de oferta enviada (mockup "Espera de Aceptación"): tarjeta
 * centrada en tono ámbar mientras el vendedor responde.
 */
const PaginaEsperaOferta = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();

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
          tono="ambar"
          titulo="Oferta Enviada"
          subtitulo="Tu oferta fue enviada. Esperando respuesta del vendedor."
          tituloResumen="Resumen de tu oferta"
          etiquetaMonto="Tu Oferta"
          tipoMaterial="Cartón corrugado"
          pesoKg={10}
          monto="$0.50/kg"
        />

        <VStack gap={3} mt={8}>
          {/* TODO: al integrar el backend, esta navegación dependerá de la aceptación real. */}
          <Button
            w="100%"
            colorPalette="verde"
            bg="marca.primario"
            rounded="lg"
            onClick={() => navigate(`/${rol}/coordinar/${id ?? "1"}`)}
          >
            Continuar (oferta aceptada)
          </Button>
          <Button
            w="100%"
            variant="outline"
            colorPalette="verde"
            rounded="lg"
            onClick={() => navigate("/seleccionar-rol")}
          >
            Volver al Inicio
          </Button>
          <Button
            w="100%"
            variant="ghost"
            colorPalette="red"
            rounded="lg"
            onClick={() => navigate(-1)}
          >
            Cancelar Oferta
          </Button>
        </VStack>
      </Box>
    </Flex>
  );
};

export default PaginaEsperaOferta;
