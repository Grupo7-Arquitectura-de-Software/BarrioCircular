import { Box, Button, Flex, HStack, Link, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

import LogotipoApp from "@/componentes/atomos/LogotipoApp.jsx";

const enlaces = [
  { etiqueta: "Cómo funciona", href: "#como-funciona" },
  { etiqueta: "Elige tu rol", href: "#roles" },
];

const LandingFooter = () => {
  const navigate = useNavigate();
  const anioActual = new Date().getFullYear();

  return (
    <>
      <Box
        px={{ base: 4, md: 8 }}
        py={{ base: 10, md: 12 }}
        bgGradient="linear(135deg, verde.700, marca.primario, verde.500)"
        color="black"
        position="relative"
        overflow="hidden"
      >
        <Box
          aria-hidden="true"
          pointerEvents="none"
          position="absolute"
          w="180px"
          h="180px"
          borderRadius="full"
          bg="whiteAlpha.200"
          top="-80px"
          left="-60px"
        />
        <Box
          aria-hidden="true"
          pointerEvents="none"
          position="absolute"
          w="220px"
          h="220px"
          borderRadius="full"
          bg="whiteAlpha.100"
          right="-80px"
          bottom="-120px"
        />
        <VStack maxW="820px" mx="auto" gap={4} textAlign="center">
          <Text
            as="h2"
            fontFamily="heading"
            fontWeight="800"
            fontSize={{ base: "2xl", md: "4xl" }}
            lineHeight="1.12"
          >
            Dale una nueva oportunidad a tus materiales
          </Text>
          <Text color="black">
            Únete a BarrioCircular y conecta con una comunidad que recicla de manera organizada.
          </Text>
          <Flex gap={3} wrap="wrap" justify="center" w={{ base: "100%", sm: "auto" }}>
            <Button
              bg="white"
              color="marca.primario"
              rounded="lg"
              w={{ base: "100%", sm: "auto" }}
              onClick={() => navigate("/seleccionar-rol")}
            >
              Únete ahora
            </Button>
            <Button
              bg="white"
              color="marca.primario"
              rounded="lg"
              w={{ base: "100%", sm: "auto" }}
              onClick={() => navigate("/auth")}
            >
              Ya tengo una cuenta
            </Button>
          </Flex>
        </VStack>
      </Box>

      <Box
        as="footer"
        px={{ base: 4, md: 8 }}
        py={5}
        bg="fondo.tarjeta"
        borderTop="1px solid"
        borderColor="gray.200"
      >
        <Flex
          maxW="1180px"
          mx="auto"
          align={{ base: "center", md: "center" }}
          justify="space-between"
          gap={5}
          direction={{ base: "column", md: "row" }}
          textAlign={{ base: "center", md: "left" }}
        >
          <VStack align={{ base: "center", md: "start" }} gap={1}>
            <LogotipoApp tamanio="sm" orientacion="horizontal" />
          </VStack>
          <Text color="gray.600" fontSize="sm">
            Proyecto académico — Universidad Central del Ecuador.
          </Text>
          <HStack gap={{ base: 4, md: 5 }} wrap="wrap">
            {enlaces.map((enlace) => (
              <Link
                key={enlace.href}
                href={enlace.href}
                color="gray.600"
                fontSize="sm"
                fontWeight="600"
                _hover={{ color: "marca.primario", textDecoration: "none" }}
              >
                {enlace.etiqueta}
              </Link>
            ))}
            <Link
              as="button"
              color="gray.600"
              fontSize="sm"
              fontWeight="600"
              onClick={() => navigate("/auth")}
              _hover={{ color: "marca.primario", textDecoration: "none" }}
            >
              Iniciar sesión
            </Link>
          </HStack>
          <Text color="gray.600" fontSize="sm">
            © {anioActual} BarrioCircular.
          </Text>
        </Flex>
      </Box>
    </>
  );
};

export default LandingFooter;
