import { HStack, Text } from "@chakra-ui/react";

/**
 * Molécula: Par etiqueta-valor para mostrar datos informativos
 * @param {string} etiqueta - Nombre del campo (ej. "Material:")
 * @param {string} valor - Valor del campo (ej. "Cartón")
 * @param {boolean} negrita - Si el valor debe ir en negrita
 */
const EtiquetaInformacion = ({ etiqueta = "Campo:", valor = "—", negrita = false }) => {
  return (
    <HStack gap={2} align="flex-start" flexWrap="wrap">
      <Text fontSize="sm" color="gray.500" minW="80px" flexShrink={0}>
        {etiqueta}
      </Text>
      <Text fontSize="sm" fontWeight={negrita ? "semibold" : "normal"} color="gray.800">
        {valor}
      </Text>
    </HStack>
  );
};

export default EtiquetaInformacion;
