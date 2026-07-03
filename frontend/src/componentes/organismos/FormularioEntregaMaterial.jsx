import { useState } from "react";
import { Box, Button, Checkbox, Flex, Text, VStack } from "@chakra-ui/react";
import { MdOutlineAddAPhoto, MdCheckCircleOutline } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const ITEMS_ENTREGA = [
  { clave: "entrega", etiqueta: "Confirmo la entrega física del material" },
  { clave: "estado", etiqueta: "El material está en el estado publicado" },
  { clave: "peso", etiqueta: "El peso fue verificado con el comprador" },
];

/**
 * Confirmación de entrega del material: lista de verificación y evidencia
 * fotográfica antes de cerrar la operación.
 */
const FormularioEntregaMaterial = ({ alConfirmar }) => {
  const [confirmaciones, setConfirmaciones] = useState({});
  const todoConfirmado = ITEMS_ENTREGA.every((item) => confirmaciones[item.clave]);

  const alternar = (clave) => {
    setConfirmaciones((actuales) => ({ ...actuales, [clave]: !actuales[clave] }));
  };

  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
      <VStack gap={6} align="stretch">
        <Box>
          <Text fontWeight="600" mb={3}>
            Lista de verificación
          </Text>
          <VStack gap={3} align="stretch">
            {ITEMS_ENTREGA.map((item) => (
              <Checkbox.Root
                key={item.clave}
                checked={Boolean(confirmaciones[item.clave])}
                onCheckedChange={() => alternar(item.clave)}
                colorPalette="verde"
              >
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label fontSize="sm">{item.etiqueta}</Checkbox.Label>
              </Checkbox.Root>
            ))}
          </VStack>
        </Box>

        <Box>
          <Text fontWeight="600" mb={3}>
            Foto de confirmación (Opcional)
          </Text>
          <Flex
            direction="column"
            align="center"
            justify="center"
            gap={2}
            h="140px"
            border="1px dashed"
            borderColor="gray.300"
            borderRadius="lg"
            bg="fondo.pagina"
            cursor="pointer"
            color="gray.500"
            _hover={{ borderColor: "marca.primario" }}
          >
            <Icono componente={<MdOutlineAddAPhoto />} tamanio="2xl" color="gray.400" />
            <Text fontSize="sm">Tomar foto de la entrega</Text>
          </Flex>
        </Box>

        <Button
          size="lg"
          colorPalette="verde"
          bg="marca.primario"
          rounded="xl"
          disabled={!todoConfirmado}
          onClick={alConfirmar}
        >
          <MdCheckCircleOutline /> Confirmar Entrega
        </Button>
      </VStack>
    </Box>
  );
};

export default FormularioEntregaMaterial;
