import { useState } from "react";
import { SignIn, SignUp } from "@clerk/clerk-react";
import { Box, Button, Flex, Text, VStack } from "@chakra-ui/react";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import LogotipoApp from "../componentes/atomos/LogotipoApp";

// Estilos del sistema de diseño aplicados a los componentes de Clerk.
// La tarjeta, cabecera y tabs son propias; Clerk solo aporta el formulario.
const aparienciaClerk = {
  variables: {
    colorPrimary: "#006a3a",
    colorDanger: "#ba1a1a",
    fontFamily: "'Inter', sans-serif",
    borderRadius: "8px",
  },
  elements: {
    rootBox: { width: "100%" },
    cardBox: { width: "100%", boxShadow: "none", border: "none" },
    card: { width: "100%", boxShadow: "none", border: "none", padding: "0" },
    header: { display: "none" },
    footer: { display: "none" },
    lastAuthenticationStrategyBadge: { display: "none" },
    formButtonPrimary: {
      backgroundColor: "#006a3a",
      borderRadius: "8px",
      "&:hover": { backgroundColor: "#00552e" },
    },
  },
};

const campoOculto = { display: "none" };

const aparienciaClerkRegistro = {
  ...aparienciaClerk,
  elements: {
    ...aparienciaClerk.elements,
    formField__firstName: campoOculto,
    formField__lastName: campoOculto,
    formFieldInput__firstName: campoOculto,
    formFieldInput__lastName: campoOculto,
    formFieldLabel__firstName: campoOculto,
    formFieldLabel__lastName: campoOculto,
  },
};

const PESTANIAS = [
  { valor: "iniciar", etiqueta: "Iniciar Sesión" },
  { valor: "registrarse", etiqueta: "Registrarse" },
];

const RUTA_POST_LOGIN = "/seleccionar-rol";
const RUTA_POST_REGISTRO = "/completar-perfil";

const PaginaAutenticacion = () => {
  const [pestaniaActiva, setPestaniaActiva] = useState("iniciar");

  return (
    <DiseniodeAutenticacion relleno={0}>
      {/* Cabecera lavanda con logo y tagline */}
      <VStack bg="fondo.cabeceraTarjeta" py={8} px={6} gap={2}>
        <LogotipoApp tamanio="sm" orientacion="horizontal" />
        <Text fontSize="sm" color="gray.600">
          Únete a la comunidad. Regenera localmente.
        </Text>
      </VStack>

      <VStack p={{ base: 5, md: 6 }} gap={5} align="stretch">
        {/* Selector Iniciar Sesión / Registrarse */}
        <Flex bg="fondo.cabeceraTarjeta" p="4px" borderRadius="lg" gap="4px">
          {PESTANIAS.map(({ valor, etiqueta }) => {
            const activa = pestaniaActiva === valor;
            return (
              <Button
                key={valor}
                flex="1"
                size="sm"
                variant="plain"
                borderRadius="md"
                bg={activa ? "fondo.tarjeta" : "transparent"}
                color={activa ? "marca.primario" : "gray.600"}
                fontWeight={activa ? "600" : "500"}
                boxShadow={activa ? "sm" : "none"}
                onClick={() => setPestaniaActiva(valor)}
              >
                {etiqueta}
              </Button>
            );
          })}
        </Flex>

        <Box display="flex" justifyContent="center">
          {pestaniaActiva === "iniciar" ? (
            <SignIn
              routing="hash"
              appearance={aparienciaClerk}
              fallbackRedirectUrl={RUTA_POST_LOGIN}
              forceRedirectUrl={RUTA_POST_LOGIN}
            />
          ) : (
            <SignUp
              routing="hash"
              appearance={aparienciaClerkRegistro}
              fallbackRedirectUrl={RUTA_POST_REGISTRO}
              forceRedirectUrl={RUTA_POST_REGISTRO}
            />
          )}
        </Box>
      </VStack>
    </DiseniodeAutenticacion>
  );
};

export default PaginaAutenticacion;
