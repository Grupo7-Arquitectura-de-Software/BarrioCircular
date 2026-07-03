import {
  Badge,
  Box,
  Button,
  Field,
  Flex,
  Input,
  InputGroup,
  Text,
  Textarea,
  VStack,
} from "@chakra-ui/react";
import { MdOutlineImage, MdSend } from "react-icons/md";
import Icono from "../atomos/Icono.jsx";
import TarjetaVendedor from "../moleculas/TarjetaVendedor.jsx";

const FilaResumen = ({ etiqueta, valor, valorSecundario, destacado }) => (
  <Flex
    justify="space-between"
    align="center"
    py={3}
    borderBottom="1px solid"
    borderColor="gray.100"
  >
    <Text color="gray.600" fontSize="sm">
      {etiqueta}
    </Text>
    <Box textAlign="right">
      <Text
        fontWeight={destacado ? "700" : "600"}
        fontFamily={destacado ? "heading" : "body"}
        fontSize={destacado ? "xl" : "sm"}
        color={destacado ? "marca.primario" : "gray.900"}
      >
        {valor}
      </Text>
      {valorSecundario && (
        <Text fontSize="xs" color="marca.primario">
          {valorSecundario}
        </Text>
      )}
    </Box>
  </Flex>
);

/**
 * Formulario "Enviar Oferta" (mockup Entregable 4): monto por kg y
 * observación, con resumen del material y vendedor al costado.
 */
const FormularioRealizarOferta = ({
  tituloMaterial = "Cartón Corrugado Limpio",
  tipoMaterial = "Cartón / Papel",
  pesoKg = 450,
  ubicacion = "La Carolina",
  distancia = "A 6.5km de ti",
  mejorOferta = "$0.18/kg",
  vendedor = "Empresa Plástica S.A.",
  rotuloVendedor = "VENDEDOR (GENERADOR)",
  alEnviar,
}) => {
  return (
    <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
      {/* Formulario */}
      <VStack align="stretch" gap={5} flex="1" w="100%">
        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
          <VStack align="stretch" gap={5}>
            <Field.Root>
              <Field.Label
                fontWeight="700"
                fontSize="sm"
                textTransform="uppercase"
                letterSpacing="wide"
              >
                Monto a Ofertar ($/kg)
              </Field.Label>
              <InputGroup startElement={<Text color="gray.500">$</Text>}>
                <Input
                  placeholder="0.00"
                  type="number"
                  step="0.01"
                  size="lg"
                  bg="fondo.pagina"
                  rounded="lg"
                />
              </InputGroup>
              <Field.HelperText>La oferta actual más alta es de {mejorOferta}</Field.HelperText>
            </Field.Root>

            <Field.Root>
              <Field.Label
                fontWeight="700"
                fontSize="sm"
                textTransform="uppercase"
                letterSpacing="wide"
              >
                Observación (Opcional)
              </Field.Label>
              <Textarea
                placeholder="Añade detalles sobre la recolección, condiciones especiales, etc."
                rows={4}
                bg="fondo.pagina"
                rounded="lg"
                resize="none"
              />
            </Field.Root>
          </VStack>
        </Box>

        <Button size="lg" colorPalette="verde" bg="marca.primario" rounded="xl" onClick={alEnviar}>
          Enviar Oferta <MdSend />
        </Button>
      </VStack>

      {/* Resumen del material */}
      <VStack align="stretch" gap={5} w={{ base: "100%", lg: "360px" }} flexShrink={0}>
        <Box
          bg="fondo.tarjeta"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="xl"
          overflow="hidden"
        >
          <Box position="relative" h="170px" bg="fondo.pagina">
            <Flex h="100%" align="center" justify="center" color="gray.300">
              <Icono componente={<MdOutlineImage />} tamanio="4xl" color="gray.300" />
            </Flex>
            <Badge
              position="absolute"
              top={3}
              left={3}
              bg="fondo.tarjeta"
              borderRadius="full"
              px={2}
              py={1}
              boxShadow="sm"
            >
              ● Oferta Activa
            </Badge>
          </Box>
          <Box p={5}>
            <Text fontFamily="heading" fontWeight="700" fontSize="lg" mb={2}>
              {tituloMaterial}
            </Text>
            <FilaResumen etiqueta="Tipo de Material" valor={tipoMaterial} />
            <FilaResumen etiqueta="Peso Estimado Disponible" valor={`${pesoKg} kg`} />
            <FilaResumen etiqueta="Ubicación" valor={ubicacion} valorSecundario={distancia} />
            <FilaResumen etiqueta="Mejor Oferta Actual" valor={mejorOferta} destacado />
          </Box>
        </Box>

        <TarjetaVendedor
          rotulo={rotuloVendedor}
          nombre={vendedor}
          calificacion="4.9"
          detalleCalificacion="(84 transacciones)"
        />
      </VStack>
    </Flex>
  );
};

export default FormularioRealizarOferta;
