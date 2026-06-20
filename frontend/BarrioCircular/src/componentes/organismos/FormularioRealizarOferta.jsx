import { VStack, HStack, Box, Text } from "@chakra-ui/react";
import EtiquetaInformacion from "../moleculas/EtiquetaInformacion";
import Boton from "../atomos/Boton";
import CampoFormulario from "../moleculas/CampoFormulario";
import CampoEntrada from "../atomos/CampoEntrada";
import { Field } from "@chakra-ui/react";


const FormularioRealizarOferta = ({
    tipoMaterial = "Plástico PET",
    pesoEstimado = 15,
    precioReferencial = "$2.50/kg",
    ubicacion = "Av. América",
    alEnviar,
}) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            {/* Resumen del material */}
            <Box
                p={3}
                border="1px solid"
                borderColor="gray.200"
                borderRadius="md"
                bg="gray.50"
            >
                <Text fontSize="sm" fontWeight="bold" mb={2}>
                    Summary of material
                </Text>
                <VStack gap={1} align="stretch">
                    <EtiquetaInformacion etiqueta="Tipo Material:" valor={tipoMaterial} />
                    <EtiquetaInformacion etiqueta="Peso Est.:" valor={`${pesoEstimado}kg`} />
                    <EtiquetaInformacion etiqueta="Precio Ref.:" valor={precioReferencial} />
                    <EtiquetaInformacion etiqueta="Ubicación:" valor={ubicacion} />
                </VStack>
            </Box>

            {/* Formulario de oferta */}
            <Field.Root>
                <Field.Label fontSize="sm" fontWeight="medium">
                    Monto Ofertado:
                </Field.Label>
                <HStack gap={1}>
                    <Text fontSize="sm" color="gray.500">$</Text>
                    <CampoEntrada
                        tipo="number"
                        marcadordePosicion="0.00"
                    />
                </HStack>
            </Field.Root>

            <CampoFormulario
                etiqueta="Observación (opcional):"
                marcadorPosicion="Escriba su observación..."
            />

            <Boton
                texto="Enviar oferta"
                variante="solid"
                colorEsquema="gray"
                ancho="full"
                alHacer={alEnviar}
            />
        </VStack>
    );
};

export default FormularioRealizarOferta;
