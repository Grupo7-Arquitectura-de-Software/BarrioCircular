import { Box, VStack, HStack, Text } from "@chakra-ui/react";
import FiladeOferta from "../moleculas/FiladeOferta";

const OFERTAS_EJEMPLO = [
  {
    id: 1,
    comprador: "Reciclador Juan",
    tipoComprador: "Reciclador",
    monto: "$4.50",
    distancia: "2km",
    observacion: "Recogida rápida",
  },
  {
    id: 2,
    comprador: "Centro de Recolección Norte",
    tipoComprador: "Centro de Recolección",
    monto: "$4.80",
    distancia: "5km",
    observacion: "Recogida rápida",
  },
];

/**
 * Organismo: Lista de ofertas recibidas con tabla de cabeceras
 * @param {Array} ofertas - Lista de objetos de oferta
 * @param {function} alVerDetalle - Callback con id de la oferta
 */
const ListadeOfertas = ({ ofertas = OFERTAS_EJEMPLO, alVerDetalle }) => {
  return (
    <VStack
      gap={0}
      align="stretch"
      w="100%"
      border="1px solid"
      borderColor="gray.200"
      borderRadius="md"
    >
      {/* Cabecera */}
      <HStack
        px={3}
        py={2}
        bg="gray.50"
        borderBottom="1px solid"
        borderColor="gray.200"
        borderTopRadius="md"
      >
        <Text fontSize="xs" fontWeight="bold" color="gray.600" flex={1}>
          Comprador
        </Text>
        <Text fontSize="xs" fontWeight="bold" color="gray.600" minW="50px">
          Monto
        </Text>
        <Text fontSize="xs" fontWeight="bold" color="gray.600" minW="50px">
          Distancia
        </Text>
        <Text fontSize="xs" fontWeight="bold" color="gray.600" minW="40px">
          Acción
        </Text>
      </HStack>

      {/* Filas */}
      {ofertas.length === 0 ? (
        <Box p={6} textAlign="center">
          <Text fontSize="sm" color="gray.400">
            0 ofertas recibidas
          </Text>
        </Box>
      ) : (
        ofertas.map((oferta) => (
          <FiladeOferta
            key={oferta.id}
            comprador={oferta.comprador}
            tipoComprador={oferta.tipoComprador}
            monto={oferta.monto}
            distancia={oferta.distancia}
            observacion={oferta.observacion}
            alVerDetalle={() => alVerDetalle && alVerDetalle(oferta.id)}
          />
        ))
      )}
    </VStack>
  );
};

export default ListadeOfertas;
