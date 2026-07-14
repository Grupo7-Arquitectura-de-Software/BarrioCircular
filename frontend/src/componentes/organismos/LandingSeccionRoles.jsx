import { Box, Button, Circle, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineFactory, MdOutlineHolidayVillage, MdOutlineLocalShipping } from "react-icons/md";

const roles = [
  {
    titulo: "Ciudadano",
    descripcion: "Publica materiales reciclables y consulta el estado de tus entregas.",
    boton: "Empezar como ciudadano",
    icono: MdOutlineHolidayVillage,
    colorFondo: "verde.50",
    borde: "verde.100",
  },
  {
    titulo: "Reciclador",
    descripcion: "Reserva materiales, organiza rutas y registra las recolecciones.",
    boton: "Empezar como reciclador",
    icono: MdOutlineLocalShipping,
    destacado: true,
    colorFondo: "marca.primario",
    borde: "marca.primario",
  },
  {
    titulo: "Centro de acopio",
    descripcion: "Encuentra materiales disponibles y gestiona su recepción.",
    boton: "Empezar como centro",
    icono: MdOutlineFactory,
    colorFondo: "azul.50",
    borde: "azul.100",
  },
];

const LandingSeccionRoles = () => {
  const navigate = useNavigate();

  return (
    <Box
      as="section"
      id="roles"
      scrollMarginTop="96px"
      px={{ base: 4, md: 8 }}
      py={{ base: 10, md: 14 }}
      bg="fondo.pagina"
    >
      <VStack maxW="1180px" mx="auto" gap={8}>
        <VStack gap={2} textAlign="center" maxW="700px">
          <Text as="h2" fontFamily="heading" fontWeight="800" fontSize={{ base: "3xl", md: "4xl" }}>
            ¿Qué puedes hacer en BarrioCircular?
          </Text>
        </VStack>
        <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} gap={6} w="100%">
          {roles.map((rol) => {
            const IconoRol = rol.icono;
            return (
              <VStack
                key={rol.titulo}
                align="start"
                gap={4}
                p={5}
                bg="fondo.tarjeta"
                border="1px solid"
                borderColor={rol.destacado ? "marca.primario" : "gray.200"}
                borderRadius="xl"
                boxShadow={rol.destacado ? "lg" : "sm"}
                minH="260px"
                transition="transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease"
                css={{ "@media (prefers-reduced-motion: reduce)": { transition: "none" } }}
                _hover={{ borderColor: rol.borde, boxShadow: "lg", transform: "translateY(-2px)" }}
                _focusWithin={{ borderColor: "marca.primario", boxShadow: "lg" }}
              >
                <Circle
                  size="58px"
                  bg={rol.colorFondo}
                  color={rol.destacado ? "white" : "marca.primario"}
                  border="1px solid"
                  borderColor={rol.borde}
                >
                  <IconoRol size="31px" aria-hidden="true" />
                </Circle>
                <VStack align="start" gap={2} flex="1">
                  <Text as="h3" fontFamily="heading" fontWeight="700" fontSize="2xl">
                    {rol.titulo}
                  </Text>
                  <Text color="gray.600">{rol.descripcion}</Text>
                </VStack>
                <Button
                  colorPalette="verde"
                  variant={rol.destacado ? "solid" : "outline"}
                  bg={rol.destacado ? "marca.primario" : undefined}
                  rounded="lg"
                  mt="auto"
                  w={{ base: "100%", sm: "auto" }}
                  onClick={() => navigate("/seleccionar-rol")}
                >
                  {rol.boton}
                </Button>
              </VStack>
            );
          })}
        </SimpleGrid>
      </VStack>
    </Box>
  );
};

export default LandingSeccionRoles;
