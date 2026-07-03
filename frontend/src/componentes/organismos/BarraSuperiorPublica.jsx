import { Flex, HStack, Text } from "@chakra-ui/react";
import { MdOutlineAccountCircle, MdOutlineNotifications } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const BarraSuperiorPublica = () => {
  return (
    <Flex
      as="header"
      px={{ base: 4, md: 8 }}
      py={3}
      bg="fondo.tarjeta"
      borderBottom="1px solid"
      borderColor="gray.200"
      justify="space-between"
      align="center"
      position="sticky"
      top={0}
      zIndex={10}
    >
      <Text fontFamily="heading" fontWeight="700" fontSize="xl" color="marca.primario">
        BarrioCircular
      </Text>
      <HStack gap={4} color="gray.700">
        <Icono componente={<MdOutlineNotifications />} tamanio="xl" />
        <Icono componente={<MdOutlineAccountCircle />} tamanio="xl" />
      </HStack>
    </Flex>
  );
};

export default BarraSuperiorPublica;
