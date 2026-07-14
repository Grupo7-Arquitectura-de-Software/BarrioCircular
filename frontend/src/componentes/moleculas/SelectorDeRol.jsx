import { Button, SimpleGrid, VStack } from "@chakra-ui/react";
import Icono from "../atomos/Icono.jsx";

/**
 * Botones tipo toggle para elegir un rol ("Me registro como:").
 * Opción activa en verde sólido, inactivas con contorno, como el mockup.
 */
const SelectorDeRol = ({ opciones, valor, alCambiar }) => {
  return (
    <SimpleGrid columns={{ base: 1, sm: opciones.length }} gap={3} w="100%">
      {opciones.map((opcion) => {
        const activa = valor === opcion.valor;
        return (
          <Button
            key={opcion.valor}
            type="button"
            onClick={() => alCambiar(opcion.valor)}
            variant={activa ? "solid" : "outline"}
            colorPalette="verde"
            bg={activa ? "marca.primario" : "fondo.tarjeta"}
            color={activa ? "white" : "gray.700"}
            borderColor={activa ? "marca.primario" : "gray.300"}
            h="auto"
            py={3}
            rounded="lg"
          >
            <VStack gap={1}>
              <Icono componente={opcion.icono} tamanio="lg" />
              {opcion.etiqueta}
            </VStack>
          </Button>
        );
      })}
    </SimpleGrid>
  );
};

export default SelectorDeRol;
