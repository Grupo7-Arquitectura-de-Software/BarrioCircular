import {
  Box,
  Button,
  Field,
  Flex,
  Input,
  InputGroup,
  SimpleGrid,
  Text,
  VStack,
  createListCollection,
} from "@chakra-ui/react";
import { MdOutlineFileUpload, MdOutlineInfo, MdOutlineLocationOn } from "react-icons/md";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";
import AreaCargaImagenes from "../moleculas/AreaCargaImagenes.jsx";
import Icono from "../atomos/Icono.jsx";

const tiposMaterial = createListCollection({
  items: [
    { label: "Cartón", value: "carton" },
    { label: "Plástico PET", value: "plastico_pet" },
    { label: "Vidrio", value: "vidrio" },
    { label: "Metal", value: "metal" },
    { label: "Papel", value: "papel" },
  ],
});

const barriosQuito = createListCollection({
  items: [
    { label: "La Floresta", value: "la_floresta" },
    { label: "Cumbayá", value: "cumbaya" },
    { label: "La Carolina", value: "la_carolina" },
    { label: "La Mariscal", value: "la_mariscal" },
    { label: "Centro Histórico", value: "centro_historico" },
    { label: "Quitumbe", value: "quitumbe" },
  ],
});

const TarjetaSeccion = ({ titulo, children }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
    <Text
      fontFamily="heading"
      fontWeight="600"
      fontSize="lg"
      pb={3}
      mb={5}
      borderBottom="1px solid"
      borderColor="gray.100"
    >
      {titulo}
    </Text>
    {children}
  </Box>
);

/**
 * Formulario "Nueva Publicación" (mockup Entregable 3): tarjetas de
 * Detalles del Material y Ubicación y Multimedia.
 */
const FormularioCrearPublicacion = ({ alPublicar, alCancelar }) => {
  return (
    <VStack gap={6} align="stretch" w="100%">
      <TarjetaSeccion titulo="Detalles del Material">
        <VStack gap={5} align="stretch">
          <SimpleGrid columns={{ base: 1, md: 2 }} gap={5}>
            <Field.Root>
              <Field.Label fontWeight="600">Tipo de Material</Field.Label>
              <SelectorDesplegable
                titulo="Seleccionar categoría"
                colecciondeDatos={tiposMaterial}
                mostrarEtiqueta={false}
              />
            </Field.Root>
            <Field.Root>
              <Field.Label fontWeight="600">Peso Estimado (kg)</Field.Label>
              <Input placeholder="ej., 5" type="number" bg="fondo.pagina" rounded="lg" />
            </Field.Root>
          </SimpleGrid>

          <Field.Root>
            <Field.Label fontWeight="600">
              Precio Referencial{" "}
              <Text as="span" color="gray.500" fontWeight="400">
                (Opcional)
              </Text>{" "}
              <Icono componente={<MdOutlineInfo />} tamanio="sm" color="gray.500" />
            </Field.Label>
            <InputGroup startElement={<Text color="gray.500">$</Text>}>
              <Input placeholder="0.00" type="number" step="0.01" bg="fondo.pagina" rounded="lg" />
            </InputGroup>
          </Field.Root>
        </VStack>
      </TarjetaSeccion>

      <TarjetaSeccion titulo="Ubicación y Multimedia">
        <VStack gap={5} align="stretch">
          <Field.Root>
            <Field.Label fontWeight="600">Ubicación de Recogida (Barrios de Quito)</Field.Label>
            <SelectorDesplegable
              titulo="Seleccionar barrio"
              colecciondeDatos={barriosQuito}
              mostrarEtiqueta={false}
              iconoInicio={
                <Icono componente={<MdOutlineLocationOn />} tamanio="md" color="marca.primario" />
              }
            />
          </Field.Root>

          <Field.Root>
            <Field.Label fontWeight="600">Foto (Altamente Recomendado)</Field.Label>
            <AreaCargaImagenes maximoArchivos={3} tamanioMaximoMB={10} />
          </Field.Root>
        </VStack>
      </TarjetaSeccion>

      <Flex
        justify="flex-end"
        gap={3}
        borderTop="1px solid"
        borderColor="gray.200"
        pt={5}
        align="center"
      >
        <Button variant="ghost" colorPalette="gray" rounded="lg" onClick={alCancelar}>
          Cancelar
        </Button>
        <Button colorPalette="verde" bg="marca.primario" rounded="lg" px={5} onClick={alPublicar}>
          <MdOutlineFileUpload /> Publicar Material
        </Button>
      </Flex>
    </VStack>
  );
};

export default FormularioCrearPublicacion;
