import { SimpleGrid, Text, VStack } from "@chakra-ui/react";
import {
  MdOutlineSentimentDissatisfied,
  MdOutlineSentimentSatisfied,
  MdOutlineSentimentVerySatisfied,
} from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const ESTADOS = [
  {
    valor: "EXCELENTE",
    etiqueta: "Excelente",
    descripcion: "Limpio y bien separado",
    icono: <MdOutlineSentimentVerySatisfied />,
    color: "marca.primario",
  },
  {
    valor: "BUENO",
    etiqueta: "Bueno",
    descripcion: "Aceptable, requiere mínima limpieza",
    icono: <MdOutlineSentimentSatisfied />,
    color: "marca.secundario",
  },
  {
    valor: "MALO",
    etiqueta: "Malo",
    descripcion: "Sucio o mal clasificado",
    icono: <MdOutlineSentimentDissatisfied />,
    color: "marca.error",
  },
];

/**
 * Selector del estado del material (mockup "Verificar Material"):
 * tres tarjetas con carita según la calidad.
 */
const SelectorEstadoMaterial = ({ valor, alCambiar }) => {
  return (
    <SimpleGrid columns={{ base: 1, sm: 3 }} gap={3} w="100%">
      {ESTADOS.map((estado) => {
        const activo = estado.valor === valor;
        return (
          <VStack
            key={estado.valor}
            as="button"
            type="button"
            onClick={() => alCambiar(estado.valor)}
            border="2px solid"
            borderColor={activo ? estado.color : "gray.200"}
            bg={activo ? "fondo.cabeceraTarjeta" : "fondo.tarjeta"}
            borderRadius="lg"
            p={4}
            gap={1}
            cursor="pointer"
            transition="all 0.15s ease"
            _hover={!activo ? { borderColor: "gray.300" } : undefined}
          >
            <Icono componente={estado.icono} tamanio="2xl" color={estado.color} />
            <Text fontWeight="700" fontSize="sm">
              {estado.etiqueta}
            </Text>
            <Text fontSize="xs" color="gray.600" textAlign="center">
              {estado.descripcion}
            </Text>
          </VStack>
        );
      })}
    </SimpleGrid>
  );
};

export default SelectorEstadoMaterial;
