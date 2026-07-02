import { Box, Grid, GridItem, HStack, Text, VStack } from "@chakra-ui/react";
import { LuChevronLeft, LuChevronRight } from "react-icons/lu";
import { createListCollection } from "@chakra-ui/react";
import SelectorDesplegable from "../atomos/SelectorDesplegable";
import Boton from "../atomos/Boton";

const DIAS_SEMANA = ["D", "L", "M", "M", "J", "V", "S"];
const DIAS_MES = Array.from({ length: 30 }, (_, i) => i + 1);

const horasCita = createListCollection({
  items: [
    { label: "08:00", value: "08:00" },
    { label: "10:00", value: "10:00" },
    { label: "13:00", value: "13:00" },
    { label: "15:00", value: "15:00" },
    { label: "17:00", value: "17:00" },
  ],
});

const preferencias = createListCollection({
  items: [
    { label: "Recolección / Recepción", value: "recoleccion" },
    { label: "Solo entrega", value: "entrega" },
    { label: "Solo recepción", value: "recepcion" },
  ],
});

/**
 * Organismo: Calendario visual para coordinar fecha/hora de recolección
 * @param {string} mes - Nombre del mes a mostrar (ej. "Junario 2023")
 * @param {number} diaSeleccionado - Día actualmente seleccionado
 * @param {function} alConfirmarCita - Callback al confirmar la cita
 */
const CalendarioCoordinacion = ({
  mes = "Junario 2023",
  diaSeleccionado = 18,
  alConfirmarCita,
}) => {
  return (
    <VStack gap={3} align="stretch" w="100%">
      {/* Cabecera del mes */}
      <HStack justify="space-between" align="center">
        <Box cursor="pointer" color="gray.600">
          <LuChevronLeft size={18} />
        </Box>
        <Text fontSize="sm" fontWeight="semibold">
          {mes}
        </Text>
        <Box cursor="pointer" color="gray.600">
          <LuChevronRight size={18} />
        </Box>
      </HStack>

      {/* Días de la semana */}
      <Grid templateColumns="repeat(7, 1fr)" gap={1}>
        {DIAS_SEMANA.map((d, i) => (
          <GridItem key={i} textAlign="center">
            <Text fontSize="xs" fontWeight="bold" color="gray.500">
              {d}
            </Text>
          </GridItem>
        ))}
      </Grid>

      {/* Días del mes */}
      <Grid templateColumns="repeat(7, 1fr)" gap={1}>
        {DIAS_MES.map((dia) => (
          <GridItem key={dia} textAlign="center">
            <Box
              w="28px"
              h="28px"
              mx="auto"
              borderRadius="full"
              bg={dia === diaSeleccionado ? "gray.700" : "transparent"}
              color={dia === diaSeleccionado ? "white" : "gray.700"}
              display="flex"
              alignItems="center"
              justifyContent="center"
              cursor="pointer"
              fontSize="xs"
              _hover={{
                bg: dia === diaSeleccionado ? "gray.700" : "gray.100",
              }}
            >
              {dia}
            </Box>
          </GridItem>
        ))}
      </Grid>

      {/* Selector de hora */}
      <SelectorDesplegable titulo="Timo - 13h" colecciondeDatos={horasCita} />

      {/* Preferencia de entrega */}
      <SelectorDesplegable titulo="Preferencia delivera" colecciondeDatos={preferencias} />

      <Boton
        texto="Confirmar Cita"
        variante="solid"
        colorEsquema="gray"
        ancho="full"
        alHacer={alConfirmarCita}
      />
    </VStack>
  );
};

export default CalendarioCoordinacion;
