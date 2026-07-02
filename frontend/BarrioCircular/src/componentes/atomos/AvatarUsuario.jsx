import { Box, HStack, Text } from "@chakra-ui/react";
import { LuUser } from "react-icons/lu";

/**
 * Átomo: Avatar de usuario con inicial o ícono
 * @param {string} nombre - Nombre completo del usuario
 * @param {string} tipo - Tipo de usuario (ej. "Reciclador", "Vendedor")
 * @param {string} tamanio - sm | md | lg
 */
const AvatarUsuario = ({ nombre = "Usuario", tipo = "", tamanio = "md" }) => {
  const sizes = {
    sm: { circulo: "32px", fuente: "sm", iconSize: 14 },
    md: { circulo: "40px", fuente: "md", iconSize: 18 },
    lg: { circulo: "52px", fuente: "lg", iconSize: 22 },
  };
  const s = sizes[tamanio] || sizes.md;

  return (
    <HStack gap={2} align="center">
      <Box
        width={s.circulo}
        height={s.circulo}
        borderRadius="full"
        bg="gray.200"
        border="1px solid"
        borderColor="gray.400"
        display="flex"
        alignItems="center"
        justifyContent="center"
        flexShrink={0}
      >
        <LuUser size={s.iconSize} color="gray" />
      </Box>
      <Box>
        <Text fontSize={s.fuente} fontWeight="semibold" lineHeight="1.2">
          {nombre}
        </Text>
        {tipo && (
          <Text fontSize="xs" color="gray.500">
            (Tipo: {tipo})
          </Text>
        )}
      </Box>
    </HStack>
  );
};

export default AvatarUsuario;
