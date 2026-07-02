import { VStack, Box, Text } from "@chakra-ui/react";
import CampoFormulario from "../moleculas/CampoFormulario";
import BotonOpcionUnica from "../atomos/BotonOpcionUnica";
import Boton from "../atomos/Boton";

const estadosMaterial = [
  { value: "excelente", title: "Excelente" },
  { value: "bueno", title: "Bueno" },
  { value: "malo", title: "Malo" },
];

const FormulariodeVerificacion = ({ alConfirmar, alReportar }) => {
  return (
    <VStack gap={4} align="stretch" w="100%">
      <CampoFormulario
        etiqueta="Peso real verificado:"
        marcadorPosicion="Ingrese el peso real en kg"
      />

      <Box>
        <Text fontSize="sm" fontWeight="medium" mb={2}>
          Estado del material:
        </Text>
        <BotonOpcionUnica Titulo="" items={estadosMaterial} />
      </Box>

      <CampoFormulario
        etiqueta="Observaciones:"
        marcadorPosicion="Describa el estado del material..."
      />

      <VStack gap={2} align="stretch">
        <Boton
          texto="Confirmar operación"
          variante="solid"
          colorEsquema="gray"
          ancho="full"
          alHacer={alConfirmar}
        />
        <Boton
          texto="Reportar problema"
          variante="outline"
          colorEsquema="red"
          ancho="full"
          alHacer={alReportar}
        />
      </VStack>
    </VStack>
  );
};

export default FormulariodeVerificacion;
