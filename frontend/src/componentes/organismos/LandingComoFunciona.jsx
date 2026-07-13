import { Box, Circle, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { MdOutlineAssignment, MdOutlineCheckCircle, MdOutlineLocalShipping } from "react-icons/md";

const pasos = [
  {
    titulo: "Publica",
    descripcion: "Registra el material, su peso aproximado y la ubicación.",
    icono: MdOutlineAssignment,
  },
  {
    titulo: "Coordina la recolección",
    descripcion: "Un reciclador reserva los materiales y organiza su recorrido.",
    icono: MdOutlineLocalShipping,
  },
  {
    titulo: "Completa el ciclo",
    descripcion: "Sigue el proceso hasta que los materiales lleguen al centro de acopio.",
    icono: MdOutlineCheckCircle,
  },
];

const LandingComoFunciona = () => (
  <Box as="section" id="como-funciona" scrollMarginTop="96px" px={{ base: 4, md: 8 }} py={{ base: 10, md: 14 }} bg="fondo.tarjeta">
    <VStack maxW="1180px" mx="auto" gap={8}>
      <VStack gap={2} textAlign="center" maxW="680px">
        <Text as="h2" fontFamily="heading" fontWeight="800" fontSize={{ base: "3xl", md: "4xl" }}>
          Reciclar puede ser más sencillo
        </Text>
        <Text color="gray.700">
          Organizamos el proceso para que los materiales reciclables lleguen desde tu comunidad hasta quienes pueden aprovecharlos.
        </Text>
      </VStack>
      <Box position="relative" w="100%">
        <Box
          aria-hidden="true"
          pointerEvents="none"
          display={{ base: "none", md: "block" }}
          position="absolute"
          top="42px"
          left="13%"
          right="13%"
          h="2px"
          bg="verde.100"
        />
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={5} w="100%" position="relative">
        {pasos.map((paso, indice) => {
          const IconoPaso = paso.icono;
          return (
            <VStack
              key={paso.titulo}
              align="start"
              gap={3}
              p={5}
              minH="220px"
              bg="fondo.pagina"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              boxShadow="sm"
              transition="transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease"
              css={{ "@media (prefers-reduced-motion: reduce)": { transition: "none" } }}
              _hover={{ borderColor: "marca.primario", boxShadow: "md", transform: "translateY(-2px)" }}
              _focusWithin={{ borderColor: "marca.primario", boxShadow: "md" }}
            >
              <Circle size="42px" bg="marca.primario" color="white" fontWeight="800">
                {indice + 1}
              </Circle>
              <IconoPaso size="28px" color="#006a3a" aria-hidden="true" />
              <Text as="h3" fontFamily="heading" fontWeight="700" fontSize="xl">
                {paso.titulo}
              </Text>
              <Text color="gray.600">{paso.descripcion}</Text>
            </VStack>
          );
        })}
        </SimpleGrid>
      </Box>
    </VStack>
  </Box>
);

export default LandingComoFunciona;
