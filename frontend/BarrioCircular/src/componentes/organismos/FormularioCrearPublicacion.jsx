import { VStack, Box, Text } from "@chakra-ui/react";
import { createListCollection } from "@chakra-ui/react";
import SelectorDesplegable from "../atomos/SelectorDesplegable";
import CampoFormulario from "../moleculas/CampoFormulario";
import AreaCargaImagenes from "../moleculas/AreaCargaImagenes";
import Boton from "../atomos/Boton";

const tiposMaterial = createListCollection({
    items: [
        { label: "Cartón", value: "carton" },
        { label: "Plástico PET", value: "plastico_pet" },
        { label: "Vidrio", value: "vidrio" },
        { label: "Metal", value: "metal" },
        { label: "Papel", value: "papel" },
    ],
});


const FormularioCrearPublicacion = ({ alPublicar }) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            <Box>
                <Text fontSize="xs" color="gray.500" mb={1}>
                    Tipo de material (e.g., Cartón)
                </Text>
                <SelectorDesplegable
                    titulo="Tipo de material"
                    colecciondeDatos={tiposMaterial}
                />
            </Box>

            <CampoFormulario
                etiqueta="Peso estimado (kg)"
                marcadorPosicion="e.g., 10"
            />

            <CampoFormulario
                etiqueta="Precio referencial (e.g., $5.00)"
                marcadorPosicion="$0.00"
            />

            <AreaCargaImagenes etiqueta="Upload Foto" maximoArchivos={3} />

            <CampoFormulario
                etiqueta="Ubicación de recogida"
                marcadorPosicion="Quito Map/Address"
            />

            <Boton
                texto="Publicar"
                variante="solid"
                colorEsquema="gray"
                ancho="full"
                alHacer={alPublicar}
            />
        </VStack>
    );
};

export default FormularioCrearPublicacion;