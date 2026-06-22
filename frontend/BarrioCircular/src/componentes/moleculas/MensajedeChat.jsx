import { Box, Text } from "@chakra-ui/react";

const MensajedeChat = ({
    texto = "Mensaje de ejemplo",
    emisor = "otro",
    hora = "14:30",
    nombreEmisor = "",
}) => {
    const esPropio = emisor === "propio";

    return (
        <Box
            display="flex"
            justifyContent={esPropio ? "flex-end" : "flex-start"}
            mb={2}
        >
            <Box
                maxW="75%"
                bg={esPropio ? "gray.700" : "gray.100"}
                color={esPropio ? "white" : "gray.800"}
                px={3}
                py={2}
                borderRadius="lg"
                borderBottomRightRadius={esPropio ? "2px" : "lg"}
                borderBottomLeftRadius={esPropio ? "lg" : "2px"}
            >
                {nombreEmisor && !esPropio && (
                    <Text fontSize="xs" fontWeight="bold" color="gray.500" mb={0.5}>
                        {nombreEmisor}
                    </Text>
                )}
                <Text fontSize="sm">{texto}</Text>
                <Text fontSize="xs" color={esPropio ? "gray.300" : "gray.400"} textAlign="right" mt={0.5}>
                    {hora}
                </Text>
            </Box>
        </Box>
    );
};

export default MensajedeChat;