import {
  Box,
  Button,
  Field,
  Flex,
  HStack,
  Input,
  Text,
  createListCollection,
} from "@chakra-ui/react";
import { MdOutlineTune } from "react-icons/md";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";
import { TIPOS_RESIDUO } from "@/utilidades/catalogoMateriales";

const tiposMaterial = createListCollection({
  items: [{ label: "Todos los materiales", value: "TODOS" }, ...TIPOS_RESIDUO],
});

const distancias = createListCollection({
  items: [
    { label: "5 km", value: "5" },
    { label: "10 km", value: "10" },
    { label: "20 km", value: "20" },
    { label: "Sin límite", value: "0" },
  ],
});

/**
 * Barra de filtros del buscador de materiales (mockup "Abastecimiento de
 * Materiales"): tipo de material, rango de peso y distancia máxima.
 */
const FormularioBuscarMateriales = ({ alBuscar }) => {
  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
      <Flex gap={4} wrap="wrap" align="flex-end">
        <Field.Root flex="2" minW="220px">
          <Field.Label fontWeight="600">Tipo de Material</Field.Label>
          <SelectorDesplegable
            titulo="Todos los materiales"
            colecciondeDatos={tiposMaterial}
            mostrarEtiqueta={false}
          />
        </Field.Root>

        <Field.Root flex="2" minW="200px">
          <Field.Label fontWeight="600">Rango de Peso (kg)</Field.Label>
          <HStack gap={2}>
            <Input placeholder="Mín" type="number" bg="fondo.pagina" rounded="lg" />
            <Text color="gray.500">-</Text>
            <Input placeholder="Máx" type="number" bg="fondo.pagina" rounded="lg" />
          </HStack>
        </Field.Root>

        <Field.Root flex="2" minW="160px">
          <Field.Label fontWeight="600">Distancia Máxima</Field.Label>
          <SelectorDesplegable
            titulo="5 km"
            colecciondeDatos={distancias}
            mostrarEtiqueta={false}
          />
        </Field.Root>

        <Button variant="outline" colorPalette="azul" rounded="lg" onClick={alBuscar}>
          <MdOutlineTune /> Más Filtros
        </Button>
      </Flex>
    </Box>
  );
};

export default FormularioBuscarMateriales;
