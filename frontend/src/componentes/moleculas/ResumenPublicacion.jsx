import { VStack, HStack, Text } from "@chakra-ui/react";
import { LuMapPin } from "react-icons/lu";

const ResumenPublicacion = ({
  tipoMaterial = "Cartón",
  pesoKg = 10,
  precio = "$5.00",
  ubicacion = "La Mariscal, Quito",
}) => {
  return (
    <VStack align="flex-start" gap={1}>
      <Text fontSize="xl" fontWeight="bold">
        {tipoMaterial}
      </Text>
      <Text fontSize="sm" color="gray.600">
        {pesoKg}kg
      </Text>
      <Text fontSize="sm" fontWeight="medium">
        {precio}
      </Text>
      <HStack gap={1} color="gray.500">
        <LuMapPin size={14} />
        <Text fontSize="xs">{ubicacion}</Text>
      </HStack>
    </VStack>
  );
};

export default ResumenPublicacion;
