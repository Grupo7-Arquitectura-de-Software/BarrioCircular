import { Box, VStack, HStack, Text } from "@chakra-ui/react";
import { LuBox } from "react-icons/lu";
import EtiquetadeEstado from "../atomos/EtiquetadeEstado";
import ResumenPublicacion from "../moleculas/ResumenPublicacion";
import Boton from "../atomos/Boton";

const TarjetaPublicacion = ({
    tipoMaterial = "Cartón",
    pesoKg = 10,
    precio = "$5.00",
    ubicacion = "La Mariscal",
    estado = "Disponible",
    cantidadOfertas = 0,
    alVerOfertas,
}) => {
    const colorEstado = {
        Disponible: "green",
        Reservado: "blue",
        Enrevision: "yellow",
        Completada: "green",
        Cancelada: "red",
    }[estado] || "gray";

    return (
        <VStack gap={3} align="stretch" w="100%">
            {/* Imagen placeholder */}
            <Box
                w="100%"
                h="160px"
                bg="gray.200"
                borderRadius="md"
                display="flex"
                alignItems="center"
                justifyContent="center"
                color="gray.400"
                border="1px solid"
                borderColor="gray.300"
            >
                <VStack gap={1}>
                    <LuBox size={40} />
                    <Text fontSize="xs" color="gray.400">
                        Foto del material
                    </Text>
                </VStack>
            </Box>

            {/* Datos */}
            <ResumenPublicacion
                tipoMaterial={tipoMaterial}
                pesoKg={pesoKg}
                precio={precio}
                ubicacion={ubicacion}
            />

            {/* Estado y ofertas */}
            <HStack justify="space-between" align="center">
                <EtiquetadeEstado color={colorEstado} texto={estado} />
                <Text fontSize="xs" color="gray.400">
                    {cantidadOfertas} oferta{cantidadOfertas !== 1 ? "s" : ""}
                </Text>
            </HStack>

            {alVerOfertas && (
                <Boton
                    texto="Ver Ofertas"
                    variante="outline"
                    ancho="full"
                    alHacer={alVerOfertas}
                />
            )}
        </VStack>
    );
};

export default TarjetaPublicacion;