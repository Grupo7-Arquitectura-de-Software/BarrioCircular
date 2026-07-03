import {
  Box,
  Button,
  Field,
  Input,
  InputGroup,
  Text,
  Textarea,
  VStack,
  createListCollection,
} from "@chakra-ui/react";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";

const estadosMaterial = createListCollection({
  items: [
    { label: "Óptimo - Listo para procesar", value: "OPTIMO" },
    { label: "Aceptable - Requiere limpieza", value: "ACEPTABLE" },
    { label: "Deficiente - Requiere reclasificación", value: "DEFICIENTE" },
  ],
});

/**
 * Formulario "Confirmar Recepción" (mockup Entregable 4): peso real recibido,
 * estado del material y observaciones adicionales.
 */
const FormulariodeVerificacion = ({
  pesoEstimadoKg = 250,
  etiquetaConfirmar = "Confirmar Recepción",
  alConfirmar,
}) => {
  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="verde.200" borderRadius="xl" p={6}>
      <VStack align="stretch" gap={5}>
        <Field.Root>
          <Field.Label fontWeight="600">Peso Real Recibido (kg)</Field.Label>
          <InputGroup endElement={<Text color="gray.500">kg</Text>}>
            <Input
              type="number"
              defaultValue={pesoEstimadoKg}
              size="lg"
              bg="fondo.pagina"
              rounded="lg"
            />
          </InputGroup>
          <Field.HelperText>Peso estimado original: {pesoEstimadoKg} kg</Field.HelperText>
        </Field.Root>

        <Field.Root>
          <Field.Label fontWeight="600">Estado del Material</Field.Label>
          <SelectorDesplegable
            titulo="Óptimo - Listo para procesar"
            colecciondeDatos={estadosMaterial}
            mostrarEtiqueta={false}
          />
        </Field.Root>

        <Field.Root>
          <Field.Label fontWeight="600">Observaciones Adicionales (Opcional)</Field.Label>
          <Textarea
            placeholder="Añade cualquier detalle sobre la calidad, humedad o variaciones en el material..."
            rows={4}
            bg="fondo.pagina"
            rounded="lg"
            resize="none"
          />
        </Field.Root>

        <Button
          size="lg"
          colorPalette="verde"
          bg="marca.primario"
          rounded="xl"
          borderTop="1px solid"
          onClick={alConfirmar}
        >
          {etiquetaConfirmar}
        </Button>
      </VStack>
    </Box>
  );
};

export default FormulariodeVerificacion;
