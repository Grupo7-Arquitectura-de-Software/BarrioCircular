import { Group, RadioCard } from "@chakra-ui/react";

const BotonOpcionUnica = ({ Titulo, items }) => {
  return (
    <RadioCard.Root defaultValue="next" gap="4" maxW="sm">
      <RadioCard.Label>{Titulo}</RadioCard.Label>
      <Group attached orientation="vertical">
        {items.map((item) => (
          <RadioCard.Item key={item.value} value={item.value} width="full">
            <RadioCard.ItemHiddenInput />
            <RadioCard.ItemControl>
              <RadioCard.ItemIndicator />
              <RadioCard.ItemContent>
                <RadioCard.ItemText>{item.title}</RadioCard.ItemText>
              </RadioCard.ItemContent>
            </RadioCard.ItemControl>
          </RadioCard.Item>
        ))}
      </Group>
    </RadioCard.Root>
  );
};

export default BotonOpcionUnica;
