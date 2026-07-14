import { Box, HStack, Text } from "@chakra-ui/react";
import { MdDoneAll } from "react-icons/md";

/**
 * Burbuja de chat del sistema de diseño: mensajes propios en verde,
 * ajenos en blanco y avisos de sistema como píldora centrada.
 */
const MensajedeChat = ({ texto = "", emisor = "otro", hora = "" }) => {
  if (emisor === "sistema") {
    return (
      <Box display="flex" justifyContent="center" my={2}>
        <Text
          fontSize="xs"
          color="gray.600"
          bg="fondo.cabeceraTarjeta"
          px={3}
          py={1}
          borderRadius="full"
          textAlign="center"
        >
          {texto}
        </Text>
      </Box>
    );
  }

  const esPropio = emisor === "propio";

  return (
    <Box display="flex" justifyContent={esPropio ? "flex-end" : "flex-start"} mb={2}>
      <Box
        maxW="75%"
        bg={esPropio ? "marca.primario" : "fondo.tarjeta"}
        color={esPropio ? "white" : "gray.800"}
        border={esPropio ? "none" : "1px solid"}
        borderColor="gray.200"
        px={4}
        py={2.5}
        borderRadius="xl"
        borderBottomRightRadius={esPropio ? "sm" : "xl"}
        borderBottomLeftRadius={esPropio ? "xl" : "sm"}
        boxShadow="xs"
      >
        <Text fontSize="sm">{texto}</Text>
        {hora && (
          <HStack justify="flex-end" gap={1} mt={1}>
            <Text fontSize="xs" color={esPropio ? "verde.200" : "gray.400"}>
              {hora}
            </Text>
            {esPropio && <MdDoneAll size={13} color="var(--chakra-colors-verde-200)" />}
          </HStack>
        )}
      </Box>
    </Box>
  );
};

export default MensajedeChat;
