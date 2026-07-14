import { HStack } from "@chakra-ui/react";
import CajadeSeleccion from "../atomos/CajadeSeleccion";

const ItemVerificacion = ({ etiqueta = "Confirmar ítem" }) => {
  return (
    <HStack gap={2} py={1}>
      <CajadeSeleccion Descripcion={etiqueta} />
    </HStack>
  );
};

export default ItemVerificacion;
