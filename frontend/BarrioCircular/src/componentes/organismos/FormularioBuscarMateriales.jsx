import {VStack, HStack, Box, Text} from "@chakra-ui/react";
import {LuSearch} from "react-icons/lu";
import FiltrodeMaterial from "../moleculas/FiltrodeMaterial";
import Boton from "../atomos/Boton";
import Divisor from "../atomos/Divisor";

const RESULTADOS_EJEMPLO = [
    {id: 1, tipo: "Cartón", cantidad: "250kg", precio: "$0.10"},
    {id: 2, tipo: "Cartón", cantidad: "500kg", precio: "$0.00"},
    {id: 3, tipo: "Maíz", cantidad: "250kg", precio: "$25.00"},
];

const FormularioBuscarMateriales = ({alBuscar}) => {
    return (
        <VStack gap={4} align="stretch" w="100%">
            <Text fontSize="md" fontWeight="bold" textAlign="center">
                BUSCAR MATERIALES
            </Text>

            <FiltrodeMaterial/>

            <Boton
                texto="Botón Buscar"
                variante="solid"
                colorEsquema="gray"
                alHacer={alBuscar}
                icono={<LuSearch style={{marginRight: 4}}/>}
            />

            <Divisor/>

            {/* Resultados de búsqueda */}
            <VStack gap={0} align="stretch" border="1px solid" borderColor="gray.200" borderRadius="md">
                {RESULTADOS_EJEMPLO.map((r) => (
                    <HStack
                        key={r.id}
                        px={3}
                        py={2}
                        borderBottom="1px solid"
                        borderColor="gray.100"
                        justify="space-between"
                        _hover={{bg: "gray.50"}}
                    >
                        <HStack gap={2}>
                            <Box w="16px" h="16px" border="1px solid" borderColor="gray.400"/>
                            <Text fontSize="sm">{r.tipo}</Text>
                            <Text fontSize="sm" color="gray.600">
                                {r.cantidad}
                            </Text>
                        </HStack>
                        <Text fontSize="sm" fontWeight="medium">
                            {r.precio}
                        </Text>
                    </HStack>
                ))}
            </VStack>
        </VStack>
    );
};

export default FormularioBuscarMateriales;
