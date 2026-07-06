import { useState } from "react";
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
import { MdOutlineFileUpload, MdOutlineLocationOn } from "react-icons/md";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";
import AreaCargaImagenes from "../moleculas/AreaCargaImagenes.jsx";
import Icono from "../atomos/Icono.jsx";
import { toaster } from "@/components/ui/toaster-instance";
import {
  BARRIOS_QUITO,
  ETIQUETAS_TIPO_RESIDUO,
  obtenerCoordenadasDeBarrio,
} from "@/utilidades/barriosQuito";

const tiposMaterial = createListCollection({
  items: Object.entries(ETIQUETAS_TIPO_RESIDUO).map(([value, label]) => ({ label, value })),
});

const barriosQuito = createListCollection({
  items: BARRIOS_QUITO.map((barrio) => ({ label: barrio.etiqueta, value: barrio.valor })),
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
 * Formulario "Nueva Publicación": recolecta los datos alineados al contrato
 * del backend (POST /api/publicaciones) y los entrega vía `alPublicar`.
 */
const FormularioCrearPublicacion = ({ alPublicar, alCancelar, estaEnviando = false }) => {
  const [tipoResiduo, setTipoResiduo] = useState("");
  const [pesoKg, setPesoKg] = useState("");
  const [precioPorKilo, setPrecioPorKilo] = useState("");
  const [barrio, setBarrio] = useState("");
  const [archivoEvidencia, setArchivoEvidencia] = useState(null);

  const advertir = (titulo, descripcion) => {
    toaster.create({ title: titulo, description: descripcion, type: "warning", duration: 3500 });
  };

  const enviarFormulario = (evento) => {
    evento.preventDefault();

    if (!tipoResiduo) {
      advertir("Selecciona el tipo de material", "Elige una categoría del catálogo.");
      return;
    }
    if (!(Number(pesoKg) > 0)) {
      advertir("Peso inválido", "El peso estimado debe ser mayor que 0 kg.");
      return;
    }
    if (!(Number(precioPorKilo) > 0)) {
      advertir("Precio inválido", "El precio por kilo debe ser mayor que 0.");
      return;
    }
    if (!barrio) {
      advertir("Selecciona la ubicación", "Indica el barrio de recogida.");
      return;
    }
    if (!archivoEvidencia) {
      advertir("Falta la foto de evidencia", "Sube una foto del material para publicar.");
      return;
    }

    alPublicar?.({
      tipoResiduo,
      pesoKg: Number(pesoKg),
      precioPorKilo: Number(precioPorKilo),
      ...obtenerCoordenadasDeBarrio(barrio),
      archivoEvidencia,
    });
  };

  return (
    <VStack as="form" onSubmit={enviarFormulario} gap={6} align="stretch" w="100%">
      <TarjetaSeccion titulo="Detalles del Material">
        <VStack gap={5} align="stretch">
          <SimpleGrid columns={{ base: 1, md: 2 }} gap={5}>
            <Field.Root required>
              <Field.Label fontWeight="600">Tipo de Material</Field.Label>
              <SelectorDesplegable
                titulo="Seleccionar categoría"
                colecciondeDatos={tiposMaterial}
                mostrarEtiqueta={false}
                valor={tipoResiduo}
                alCambiar={setTipoResiduo}
              />
            </Field.Root>
            <Field.Root required>
              <Field.Label fontWeight="600">Peso Estimado (kg)</Field.Label>
              <Input
                placeholder="ej., 5"
                type="number"
                min="0.1"
                step="0.1"
                bg="fondo.pagina"
                rounded="lg"
                value={pesoKg}
                onChange={(evento) => setPesoKg(evento.target.value)}
                required
              />
            </Field.Root>
          </SimpleGrid>

          <Field.Root required>
            <Field.Label fontWeight="600">Precio por Kilo</Field.Label>
            <InputGroup startElement={<Text color="gray.500">$</Text>}>
              <Input
                placeholder="0.00"
                type="number"
                min="0.01"
                step="0.01"
                bg="fondo.pagina"
                rounded="lg"
                value={precioPorKilo}
                onChange={(evento) => setPrecioPorKilo(evento.target.value)}
                required
              />
            </InputGroup>
          </Field.Root>
        </VStack>
      </TarjetaSeccion>

      <TarjetaSeccion titulo="Ubicación y Multimedia">
        <VStack gap={5} align="stretch">
          <Field.Root required>
            <Field.Label fontWeight="600">Ubicación de Recogida (Barrios de Quito)</Field.Label>
            <SelectorDesplegable
              titulo="Seleccionar barrio"
              colecciondeDatos={barriosQuito}
              mostrarEtiqueta={false}
              valor={barrio}
              alCambiar={setBarrio}
              iconoInicio={
                <Icono componente={<MdOutlineLocationOn />} tamanio="md" color="marca.primario" />
              }
            />
          </Field.Root>

          <Field.Root required>
            <Field.Label fontWeight="600">Foto de Evidencia</Field.Label>
            <AreaCargaImagenes
              maximoArchivos={1}
              tamanioMaximoMB={10}
              alCambiarArchivos={({ acceptedFiles }) =>
                setArchivoEvidencia(acceptedFiles[0] ?? null)
              }
            />
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
        <Button
          variant="ghost"
          colorPalette="gray"
          rounded="lg"
          onClick={alCancelar}
          disabled={estaEnviando}
        >
          Cancelar
        </Button>
        <Button
          type="submit"
          colorPalette="verde"
          bg="marca.primario"
          rounded="lg"
          px={5}
          loading={estaEnviando}
          loadingText="Publicando"
        >
          <MdOutlineFileUpload /> Publicar Material
        </Button>
      </Flex>
    </VStack>
  );
};

export default FormularioCrearPublicacion;
