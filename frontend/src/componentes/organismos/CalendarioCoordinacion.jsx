import { useMemo, useState } from "react";
import { Box, Button, Circle, Flex, Grid, GridItem, HStack, Text, VStack } from "@chakra-ui/react";
import {
  MdCheckCircle,
  MdChevronLeft,
  MdChevronRight,
  MdOutlineCalendarToday,
  MdOutlineLocalShipping,
  MdOutlineSchedule,
} from "react-icons/md";
import Icono from "../atomos/Icono.jsx";

const DIAS_SEMANA = ["Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"];
const HORAS = ["09:00", "10:30", "13:00", "14:30", "16:00", "17:30"];
const MODALIDADES = [
  { valor: "RECOLECCION", titulo: "Recolección", descripcion: "Nosotros recogemos el material." },
  { valor: "RECEPCION", titulo: "Recepción", descripcion: "El vendedor lo trae al centro." },
];

const construirDiasDelMes = (anio, mes) => {
  // Celdas del calendario alineadas a semanas que inician en lunes.
  const primerDia = new Date(anio, mes, 1);
  const desplazamiento = (primerDia.getDay() + 6) % 7;
  const totalDias = new Date(anio, mes + 1, 0).getDate();
  const diasMesAnterior = new Date(anio, mes, 0).getDate();

  const celdas = [];
  for (let i = desplazamiento - 1; i >= 0; i--) {
    celdas.push({ dia: diasMesAnterior - i, fueraDeMes: true });
  }
  for (let dia = 1; dia <= totalDias; dia++) {
    celdas.push({ dia, fueraDeMes: false });
  }
  return celdas;
};

/**
 * Coordinación de logística (mockup "Coordinar Recolección"): calendario del
 * mes, horarios disponibles y modalidad del intercambio.
 */
const CalendarioCoordinacion = ({ alConfirmarCita }) => {
  const hoy = useMemo(() => new Date(), []);
  const [fechaVisible, setFechaVisible] = useState(
    () => new Date(hoy.getFullYear(), hoy.getMonth(), 1),
  );
  const [diaSeleccionado, setDiaSeleccionado] = useState(hoy.getDate());
  const [horaSeleccionada, setHoraSeleccionada] = useState("13:00");
  const [modalidad, setModalidad] = useState("RECOLECCION");

  const celdas = useMemo(
    () => construirDiasDelMes(fechaVisible.getFullYear(), fechaVisible.getMonth()),
    [fechaVisible],
  );

  const nombreMes = fechaVisible.toLocaleDateString("es-EC", { month: "long", year: "numeric" });

  const cambiarMes = (delta) => {
    setFechaVisible((actual) => new Date(actual.getFullYear(), actual.getMonth() + delta, 1));
    setDiaSeleccionado(null);
  };

  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
      <VStack align="stretch" gap={6}>
        {/* Fecha */}
        <Box>
          <Flex justify="space-between" align="center" mb={3}>
            <HStack gap={2}>
              <Icono componente={<MdOutlineCalendarToday />} tamanio="lg" color="marca.primario" />
              <Text fontWeight="700">Fecha</Text>
            </HStack>
            <HStack gap={1}>
              <Button
                variant="ghost"
                size="xs"
                onClick={() => cambiarMes(-1)}
                aria-label="Mes anterior"
              >
                <MdChevronLeft />
              </Button>
              <Text
                fontWeight="600"
                fontSize="sm"
                textTransform="capitalize"
                minW="120px"
                textAlign="center"
              >
                {nombreMes}
              </Text>
              <Button
                variant="ghost"
                size="xs"
                onClick={() => cambiarMes(1)}
                aria-label="Mes siguiente"
              >
                <MdChevronRight />
              </Button>
            </HStack>
          </Flex>

          <Box bg="fondo.pagina" borderRadius="lg" p={4}>
            <Grid templateColumns="repeat(7, 1fr)" gap={1} mb={2}>
              {DIAS_SEMANA.map((dia) => (
                <GridItem key={dia} textAlign="center">
                  <Text fontSize="xs" fontWeight="600" color="gray.500">
                    {dia}
                  </Text>
                </GridItem>
              ))}
            </Grid>
            <Grid templateColumns="repeat(7, 1fr)" gap={1}>
              {celdas.map(({ dia, fueraDeMes }, indice) => {
                const activo = !fueraDeMes && dia === diaSeleccionado;
                return (
                  <GridItem key={`${indice}-${dia}`} textAlign="center">
                    <Circle
                      size="34px"
                      mx="auto"
                      bg={activo ? "marca.primario" : "transparent"}
                      color={fueraDeMes ? "gray.300" : activo ? "white" : "gray.700"}
                      fontWeight={activo ? "700" : "400"}
                      fontSize="sm"
                      cursor={fueraDeMes ? "default" : "pointer"}
                      _hover={!fueraDeMes && !activo ? { bg: "fondo.cabeceraTarjeta" } : undefined}
                      onClick={() => !fueraDeMes && setDiaSeleccionado(dia)}
                    >
                      {dia}
                    </Circle>
                  </GridItem>
                );
              })}
            </Grid>
          </Box>
        </Box>

        {/* Hora */}
        <Box>
          <HStack gap={2} mb={3}>
            <Icono componente={<MdOutlineSchedule />} tamanio="lg" color="marca.primario" />
            <Text fontWeight="700">Hora</Text>
          </HStack>
          <Flex gap={2} wrap="wrap">
            {HORAS.map((hora) => {
              const activa = hora === horaSeleccionada;
              return (
                <Button
                  key={hora}
                  size="sm"
                  variant={activa ? "outline" : "outline"}
                  colorPalette={activa ? "verde" : "gray"}
                  borderColor={activa ? "marca.primario" : "gray.300"}
                  color={activa ? "marca.primario" : "gray.700"}
                  fontWeight={activa ? "700" : "400"}
                  rounded="lg"
                  onClick={() => setHoraSeleccionada(hora)}
                >
                  {activa && <MdCheckCircle />} {hora}
                </Button>
              );
            })}
          </Flex>
        </Box>

        {/* Modalidad */}
        <Box>
          <HStack gap={2} mb={3}>
            <Icono componente={<MdOutlineLocalShipping />} tamanio="lg" color="marca.primario" />
            <Text fontWeight="700">Modalidad</Text>
          </HStack>
          <Flex gap={3} direction={{ base: "column", sm: "row" }}>
            {MODALIDADES.map((opcion) => {
              const activa = opcion.valor === modalidad;
              return (
                <Flex
                  key={opcion.valor}
                  as="button"
                  type="button"
                  onClick={() => setModalidad(opcion.valor)}
                  flex="1"
                  border="2px solid"
                  borderColor={activa ? "marca.primario" : "gray.200"}
                  borderRadius="lg"
                  p={4}
                  gap={3}
                  justify="space-between"
                  align="flex-start"
                  textAlign="left"
                  cursor="pointer"
                  bg="fondo.tarjeta"
                >
                  <Box>
                    <Text fontWeight="700" fontSize="sm">
                      {opcion.titulo}
                    </Text>
                    <Text fontSize="xs" color="gray.600">
                      {opcion.descripcion}
                    </Text>
                  </Box>
                  {activa ? (
                    <Icono componente={<MdCheckCircle />} tamanio="lg" color="marca.primario" />
                  ) : (
                    <Circle size="18px" border="2px solid" borderColor="gray.300" />
                  )}
                </Flex>
              );
            })}
          </Flex>
        </Box>

        <Button
          size="lg"
          colorPalette="verde"
          bg="marca.primario"
          rounded="xl"
          onClick={alConfirmarCita}
        >
          Confirmar Cita
        </Button>
      </VStack>
    </Box>
  );
};

export default CalendarioCoordinacion;
