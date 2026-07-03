import { Badge, Stack } from "@chakra-ui/react";

const EtiquetadeEstado = ({ color, texto }) => {
  return (
    <Stack direction="row">
      <Badge colorPalette={color}>{texto}</Badge>
    </Stack>
  );
};

export default EtiquetadeEstado;
