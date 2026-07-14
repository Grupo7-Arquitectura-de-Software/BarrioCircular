import { Box, Circle, Flex, HStack, IconButton, Input, Text, VStack } from "@chakra-ui/react";
import {
  MdMoreVert,
  MdOutlineAddPhotoAlternate,
  MdOutlinePerson,
  MdOutlinePhone,
  MdSend,
} from "react-icons/md";
import MensajedeChat from "../moleculas/MensajedeChat.jsx";
import Icono from "../atomos/Icono.jsx";

const MENSAJES_EJEMPLO = [
  {
    id: 0,
    emisor: "sistema",
    texto: "Publicación marcada como Reservada. Coordinación iniciada.",
  },
  {
    id: 1,
    emisor: "otro",
    texto:
      "¡Hola Maria! Gracias por aceptar mi solicitud. Puedo pasar esta tarde alrededor de las 4 PM, ¿te viene bien?",
    hora: "10:42 AM",
  },
  {
    id: 2,
    emisor: "propio",
    texto:
      "¡Hola Carlos! Las 4 PM me parece perfecto. Los dejaré en la puerta principal para que puedas cogerlos.",
    hora: "10:45 AM",
  },
  {
    id: 3,
    emisor: "otro",
    texto:
      "Genial. ¿Puedes confirmar el número de la calle de nuevo? Sé que es en la Av. Amazonas.",
    hora: "10:47 AM",
  },
  {
    id: 4,
    emisor: "propio",
    texto: "Sí, es el edificio 442. Aquí tienes un pin: Av. Amazonas 442",
    hora: "10:50 AM",
  },
];

/**
 * Chat de coordinación de recolección (mockup Entregable 3): cabecera con
 * participante en línea, mensajes con burbujas del sistema de diseño y
 * entrada de mensaje con botón de envío.
 */
const ChatdeCoordinacion = ({ participante = "Carlos R.", mensajes = MENSAJES_EJEMPLO }) => {
  return (
    <Flex
      direction="column"
      h="100%"
      minH="480px"
      bg="fondo.tarjeta"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="xl"
      overflow="hidden"
    >
      {/* Cabecera del chat */}
      <Flex
        px={4}
        py={3}
        borderBottom="1px solid"
        borderColor="gray.200"
        justify="space-between"
        align="center"
      >
        <HStack gap={3}>
          <Circle size="40px" bg="fondo.cabeceraTarjeta" color="gray.600">
            <Icono componente={<MdOutlinePerson />} tamanio="xl" />
          </Circle>
          <Box>
            <Text fontWeight="600">{participante}</Text>
            <HStack gap={1}>
              <Circle size="8px" bg="verde.400" />
              <Text fontSize="xs" color="gray.600">
                En línea ahora
              </Text>
            </HStack>
          </Box>
        </HStack>
        <HStack gap={1} color="gray.600">
          <IconButton variant="ghost" size="sm" aria-label="Llamar">
            <MdOutlinePhone />
          </IconButton>
          <IconButton variant="ghost" size="sm" aria-label="Más opciones">
            <MdMoreVert />
          </IconButton>
        </HStack>
      </Flex>

      {/* Mensajes */}
      <Box flex="1" overflowY="auto" p={4}>
        <VStack gap={1} align="stretch">
          {mensajes.map((mensaje) => (
            <MensajedeChat
              key={mensaje.id}
              texto={mensaje.texto}
              emisor={mensaje.emisor}
              hora={mensaje.hora}
            />
          ))}
        </VStack>
      </Box>

      {/* Entrada de mensaje */}
      <HStack p={3} borderTop="1px solid" borderColor="gray.200" gap={2}>
        <IconButton variant="ghost" color="gray.600" aria-label="Adjuntar imagen">
          <MdOutlineAddPhotoAlternate />
        </IconButton>
        <Input placeholder="Escribe un mensaje..." bg="fondo.pagina" borderRadius="lg" flex={1} />
        <IconButton
          colorPalette="verde"
          bg="marca.primario"
          borderRadius="lg"
          aria-label="Enviar mensaje"
        >
          <MdSend />
        </IconButton>
      </HStack>
    </Flex>
  );
};

export default ChatdeCoordinacion;
