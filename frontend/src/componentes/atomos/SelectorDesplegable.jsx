"use client";
import { HStack, Portal, Select } from "@chakra-ui/react";

const SelectorDesplegable = ({
  titulo,
  colecciondeDatos,
  mostrarEtiqueta = true,
  ancho = "100%",
  iconoInicio,
}) => {
  return (
    <Select.Root collection={colecciondeDatos} size="md" width={ancho}>
      <Select.HiddenSelect />
      {mostrarEtiqueta && <Select.Label>{titulo}</Select.Label>}
      <Select.Control>
        <Select.Trigger bg="fondo.pagina" rounded="lg">
          <HStack gap={2} flex="1" minW={0}>
            {iconoInicio}
            <Select.ValueText placeholder={titulo} />
          </HStack>
        </Select.Trigger>
        <Select.IndicatorGroup>
          <Select.Indicator />
        </Select.IndicatorGroup>
      </Select.Control>
      <Portal>
        <Select.Positioner>
          <Select.Content>
            {colecciondeDatos.items.map((dato) => (
              <Select.Item item={dato} key={dato.value}>
                {dato.label}
                <Select.ItemIndicator />
              </Select.Item>
            ))}
          </Select.Content>
        </Select.Positioner>
      </Portal>
    </Select.Root>
  );
};

export default SelectorDesplegable;
