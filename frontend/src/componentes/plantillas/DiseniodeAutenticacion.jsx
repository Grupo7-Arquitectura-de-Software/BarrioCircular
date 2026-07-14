import { Box, Flex } from "@chakra-ui/react";
import BarraSuperiorPublica from "../organismos/BarraSuperiorPublica.jsx";

/**
 * Plantilla de páginas de autenticación/onboarding del sistema de diseño:
 * fondo superficie clara y tarjeta blanca centrada.
 * `relleno={0}` permite cabeceras de tarjeta al borde (ej. cabecera lavanda).
 */
const DiseniodeAutenticacion = ({
  children,
  maxW = "480px",
  relleno = { base: 6, md: 8 },
  conBarraSuperior = false,
}) => {
  return (
    <Flex direction="column" minH="100vh" w="100%" bg="fondo.pagina">
      {conBarraSuperior && <BarraSuperiorPublica />}
      <Flex flex="1" align="center" justify="center" p={{ base: 4, md: 8 }}>
        <Box
          w="100%"
          maxW={maxW}
          bg="fondo.tarjeta"
          borderRadius="xl"
          border="1px solid"
          borderColor="gray.200"
          boxShadow="md"
          overflow="hidden"
          p={relleno}
        >
          {children}
        </Box>
      </Flex>
    </Flex>
  );
};

export default DiseniodeAutenticacion;
