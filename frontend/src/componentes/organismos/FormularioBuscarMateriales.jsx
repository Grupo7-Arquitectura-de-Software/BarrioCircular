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
import { useState } from "react";

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
 * Barra de filtros del buscador de materiales.
 */
const FormularioBuscarMateriales = ({ onBuscar }) => {
  const [tipoMaterial, setTipoMaterial] = useState("TODOS");
  const [pesoMinimo, setPesoMinimo] = useState("");
  const [pesoMaximo, setPesoMaximo] = useState("");
  const [distancia, setDistancia] = useState("5");

  const manejarBusqueda = () => {
    onBuscar({ tipoMaterial, pesoMinimo, pesoMaximo, distancia });
  };

  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
      <Flex gap={4} wrap="wrap" align="flex-end">
        <Field.Root flex="2" minW="220px">
          <Field.Label fontWeight="600">Tipo de Material</Field.Label>
          <SelectorDesplegable
            titulo="Todos los materiales"
            colecciondeDatos={tiposMaterial}
            mostrarEtiqueta={false}
            valor={tipoMaterial}
            alCambiar={setTipoMaterial}
          />
        </Field.Root>

        <Field.Root flex="2" minW="200px">
          <Field.Label fontWeight="600">Rango de Peso (kg)</Field.Label>
          <HStack gap={2}>
            <Input
              placeholder="Mín"
              type="number"
              bg="fondo.pagina"
              rounded="lg"
              value={pesoMinimo}
              onChange={(e) => setPesoMinimo(e.target.value)}
            />
            <Text color="gray.500">-</Text>
            <Input
              placeholder="Máx"
              type="number"
              bg="fondo.pagina"
              rounded="lg"
              value={pesoMaximo}
              onChange={(e) => setPesoMaximo(e.target.value)}
            />
          </HStack>
        </Field.Root>

        <Field.Root flex="2" minW="160px">
          <Field.Label fontWeight="600">Distancia Máxima</Field.Label>
          <SelectorDesplegable
            titulo="5 km"
            colecciondeDatos={distancias}
            mostrarEtiqueta={false}
            valor={distancia}
            alCambiar={setDistancia}
          />
        </Field.Root>

        <Button variant="outline" colorPalette="azul" rounded="lg" onClick={manejarBusqueda}>
          <MdOutlineTune /> Filtrar
        </Button>
      </Flex>
    </Box>
  );
};

export default FormularioBuscarMateriales;
