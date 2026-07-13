import { Badge, Box, Button, Circle, Flex, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineFactory, MdOutlineHolidayVillage, MdOutlineLocalShipping } from "react-icons/md";

const actores = [
  { titulo: "Ciudadano", icono: MdOutlineHolidayVillage, texto: "Publica materiales" },
  { titulo: "Reciclador", icono: MdOutlineLocalShipping, texto: "Organiza la recolección" },
  { titulo: "Centro", icono: MdOutlineFactory, texto: "Recibe y gestiona" },
];

const LandingHero = () => {
  const navigate = useNavigate();

  return (
    <Flex
      as="section"
      id="inicio"
      scrollMarginTop="96px"
      px={{ base: 4, md: 8 }}
      py={{ base: 10, md: 14, xl: 16 }}
      justify="center"
      bg="fondo.pagina"
    >
      <SimpleGrid
        columns={{ base: 1, md: 2 }}
        gap={{ base: 8, md: 8, xl: 14 }}
        maxW="1180px"
        w="100%"
        alignItems="center"
      >
        <VStack align={{ base: "stretch", md: "start" }} gap={5} textAlign={{ base: "center", md: "left" }}>
          <Badge colorPalette="verde" variant="subtle" px={3} py={1} borderRadius="full" alignSelf={{ base: "center", md: "start" }}>
            Reciclaje conectado con tu comunidad
          </Badge>
          <VStack align={{ base: "center", md: "start" }} gap={4}>
            <Text
              as="h1"
              fontFamily="heading"
              fontWeight="800"
              fontSize={{ base: "3xl", sm: "4xl", md: "5xl", xl: "6xl" }}
              lineHeight="1.05"
              maxW="680px"
            >
              Convierte tus residuos en nuevas oportunidades
            </Text>
            <Text color="gray.700" fontSize={{ base: "md", md: "lg" }} maxW="620px">
              Conecta con recicladores y centros de acopio para publicar, recolectar y aprovechar materiales reciclables de forma organizada.
            </Text>
          </VStack>
          <Flex gap={3} wrap="wrap" w={{ base: "100%", sm: "auto" }} justify={{ base: "stretch", sm: "center", md: "start" }}>
            <Button
              colorPalette="verde"
              bg="marca.primario"
              size="lg"
              rounded="lg"
              w={{ base: "100%", sm: "auto" }}
              onClick={() => navigate("/seleccionar-rol")}
            >
              Comenzar a reciclar
            </Button>
            <Button as="a" href="#como-funciona" variant="outline" colorPalette="verde" size="lg" rounded="lg" w={{ base: "100%", sm: "auto" }}>
              Ver cómo funciona
            </Button>
          </Flex>
        </VStack>

        <Box position="relative" minH={{ base: "340px", sm: "360px", md: "390px" }} overflow="hidden">
          <Box
            position="absolute"
            inset={{ base: "16px 0", md: "14px" }}
            borderRadius="3xl"
            bgGradient="linear(135deg, verde.50, fondo.cabeceraTarjeta, azul.50)"
            border="1px solid"
            borderColor="gray.200"
          />
          <Circle
            aria-hidden="true"
            pointerEvents="none"
            position="absolute"
            size={{ base: "120px", md: "160px" }}
            bg="whiteAlpha.700"
            top="4%"
            right="-40px"
          />
          <Circle
            aria-hidden="true"
            pointerEvents="none"
            position="absolute"
            size={{ base: "90px", md: "120px" }}
            bg="verde.100"
            opacity={0.65}
            bottom="2%"
            left="-28px"
          />
          <Box
            aria-hidden="true"
            pointerEvents="none"
            position="absolute"
            top="86px"
            bottom="86px"
            left={{ base: "43px", sm: "calc(50% - 154px)", md: "72px" }}
            w="2px"
            bg="verde.200"
            opacity={0.9}
          />
          <VStack position="relative" gap={3} p={{ base: 4, md: 6 }}>
            {actores.map((actor, indice) => {
              const IconoActor = actor.icono;
              return (
                <Flex
                  key={actor.titulo}
                  w="100%"
                  maxW="380px"
                  align="center"
                  gap={4}
                  bg="fondo.tarjeta"
                  border="1px solid"
                  borderColor={indice === 1 ? "marca.primario" : "gray.200"}
                  borderRadius="2xl"
                  boxShadow={indice === 1 ? "lg" : "md"}
                  p={{ base: 3, sm: 4 }}
                  ml={{ base: 0, md: indice === 1 ? 10 : 0 }}
                  position="relative"
                >
                  <Circle size="54px" bg={indice === 1 ? "marca.primario" : "verde.50"} color={indice === 1 ? "white" : "marca.primario"} flexShrink={0}>
                    <IconoActor size="28px" aria-hidden="true" />
                  </Circle>
                  <Box flex="1">
                    <Text fontFamily="heading" fontWeight="700" fontSize="lg">
                      {actor.titulo}
                    </Text>
                    <Text color="gray.600" fontSize="sm">
                      {actor.texto}
                    </Text>
                  </Box>
                </Flex>
              );
            })}
          </VStack>
        </Box>
      </SimpleGrid>
    </Flex>
  );
};

export default LandingHero;
