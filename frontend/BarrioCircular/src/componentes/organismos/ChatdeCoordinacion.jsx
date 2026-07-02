import { VStack, Box, HStack, Input } from "@chakra-ui/react";
import { LuSend } from "react-icons/lu";
import MensajedeChat from "../moleculas/MensajedeChat";

const MENSAJES_EJEMPLO = [
  {
    id: 1,
    texto: "Hola 14:30 pocas 22:00",
    emisor: "propio",
    hora: "14:30",
    nombreEmisor: "Vendedor",
  },
  {
    id: 2,
    texto: "Hacía el tiempo y buagaciones?",
    emisor: "propio",
    hora: "14:31",
    nombreEmisor: "Vendedor",
  },
  {
    id: 3,
    texto: "Tiempo y la locación en: Maps://cncs",
    emisor: "otro",
    hora: "14:35",
    nombreEmisor: "Reciclador Juan",
  },
  { id: 4, texto: "Ho es que tiempo?", emisor: "propio", hora: "14:36", nombreEmisor: "Vendedor" },
];

const ChatdeCoordinacion = ({ mensajes = MENSAJES_EJEMPLO }) => {
  return (
    <VStack
      gap={0}
      align="stretch"
      h="100%"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="md"
    >
      {/* Área de mensajes */}
      <Box flex={1} overflowY="auto" p={3} minH="300px" bg="gray.50">
        <VStack gap={1} align="stretch">
          {mensajes.map((msg) => (
            <MensajedeChat
              key={msg.id}
              texto={msg.texto}
              emisor={msg.emisor}
              hora={msg.hora}
              nombreEmisor={msg.nombreEmisor}
            />
          ))}
        </VStack>
      </Box>

      {/* Input de mensaje */}
      <HStack
        p={2}
        borderTop="1px solid"
        borderColor="gray.200"
        bg="white"
        borderBottomRadius="md"
        gap={2}
      >
        <Input placeholder="Mensaje..." size="sm" flex={1} borderRadius="full" />
        <Box
          cursor="pointer"
          color="gray.600"
          _hover={{ color: "gray.900" }}
          display="flex"
          alignItems="center"
        >
          <LuSend size={18} />
        </Box>
      </HStack>
    </VStack>
  );
};

export default ChatdeCoordinacion;
