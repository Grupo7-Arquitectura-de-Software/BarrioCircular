import { HStack, Text, Button } from "@chakra-ui/react";

const FiladeOferta = ({
    comprador = "Reciclador Juan",
    tipoComprador = "Reciclador",
    monto = "$4.50",
    distancia = "2km",
    alVerDetalle,
}) => {
    return (
        <HStack
            justify="space-between"
            align="center"
            p={3}
            borderBottom="1px solid"
            borderColor="gray.200"
            _hover={{ bg: "gray.50" }}
        >
            <Text fontSize="sm" fontWeight="medium" minW="100px">
                {comprador}
                <Text as="span" fontSize="xs" color="gray.500" display="block">
                    {tipoComprador}
                </Text>
            </Text>
            <Text fontSize="sm" fontWeight="semibold" minW="50px">
                {monto}
            </Text>
            <Text fontSize="sm" color="gray.600" minW="40px">
                {distancia}
            </Text>
            <Button size="xs" variant="outline" onClick={alVerDetalle}>
                Ver
            </Button>
        </HStack>
    );
};

export default FiladeOferta;