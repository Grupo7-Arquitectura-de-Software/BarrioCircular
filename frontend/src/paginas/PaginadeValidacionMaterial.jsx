import { useState } from "react";
import {
  Badge,
  Box,
  Button,
  Field,
  Flex,
  HStack,
  Input,
  InputGroup,
  Link,
  Text,
  Textarea,
  VStack,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import {
  MdArrowBack,
  MdCheckCircleOutline,
  MdOutlineImage,
  MdOutlineRecycling,
} from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import SelectorEstadoMaterial from "../componentes/moleculas/SelectorEstadoMaterial.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { NAVEGACION_RECOLECTOR, SUBTITULO_RECOLECTOR } from "@/utilidades/navegacionPanel";

/**
 * Verificación de peso y estado del material recolectado
 * (mockup "Verificar Material - Reciclador").
 */
const PaginadeValidacionMaterial = () => {
  const navigate = useNavigate();
  const [estadoMaterial, setEstadoMaterial] = useState("BUENO");

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_RECOLECTOR}
      subtituloMarca={SUBTITULO_RECOLECTOR}
      mostrarBuscador={false}
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Link
            onClick={() => navigate(-1)}
            color="marca.primario"
            fontSize="sm"
            fontWeight="600"
            display="inline-flex"
            alignItems="center"
            gap={1}
            w="fit-content"
          >
            <MdArrowBack /> Volver
          </Link>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Verificar Peso y Estado del Material
          </Text>
          <Text color="gray.600">
            Confirma los detalles finales antes de completar la recolección.
          </Text>
        </VStack>

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          {/* Resumen de la oferta */}
          <Box
            w={{ base: "100%", lg: "300px" }}
            flexShrink={0}
            bg="fondo.tarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            p={5}
          >
            <Flex justify="space-between" align="flex-start" mb={1}>
              <Box>
                <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                  Oferta #103380
                </Text>
                <Text fontSize="sm" color="gray.600">
                  Juan Pérez
                </Text>
              </Box>
              <Badge
                bg="fondo.cabeceraTarjeta"
                color="marca.secundario"
                borderRadius="md"
                px={2}
                py={1}
              >
                <MdOutlineRecycling /> Plástico PET
              </Badge>
            </Flex>
            <HStack gap={4} mt={4} align="flex-start">
              <Flex
                w="88px"
                h="88px"
                bg="fondo.pagina"
                border="1px solid"
                borderColor="gray.200"
                borderRadius="lg"
                align="center"
                justify="center"
                color="gray.300"
                flexShrink={0}
              >
                <Icono componente={<MdOutlineImage />} tamanio="2xl" color="gray.300" />
              </Flex>
              <VStack align="stretch" gap={1} fontSize="sm">
                <Text>
                  <Text as="span" fontWeight="600">
                    Peso estimado:
                  </Text>{" "}
                  15 kg
                </Text>
                <Text>
                  <Text as="span" fontWeight="600">
                    Precio ref:
                  </Text>{" "}
                  $2.50/kg
                </Text>
                <Text>
                  <Text as="span" fontWeight="600">
                    Oferta acordada:
                  </Text>{" "}
                  $35.00
                </Text>
              </VStack>
            </HStack>
          </Box>

          {/* Formulario de verificación */}
          <Box
            flex="1"
            w="100%"
            bg="fondo.tarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            p={6}
          >
            <VStack align="stretch" gap={6}>
              <Field.Root>
                <Field.Label fontWeight="600">Peso real verificado (kg)</Field.Label>
                <InputGroup endElement={<Text color="gray.500">kg</Text>}>
                  <Input
                    placeholder="Ej. 14.5"
                    type="number"
                    step="0.1"
                    size="lg"
                    bg="fondo.pagina"
                    rounded="lg"
                  />
                </InputGroup>
                <Field.HelperText>
                  Pesa el material en sitio para confirmar la cantidad exacta.
                </Field.HelperText>
              </Field.Root>

              <Box>
                <Text fontWeight="600" fontSize="sm" mb={3}>
                  Estado del material
                </Text>
                <SelectorEstadoMaterial valor={estadoMaterial} alCambiar={setEstadoMaterial} />
              </Box>

              <Field.Root>
                <Field.Label fontWeight="600">Observaciones (Opcional)</Field.Label>
                <Textarea
                  placeholder="Añade notas sobre la calidad del material o incidencias durante la recolección..."
                  rows={4}
                  bg="fondo.pagina"
                  rounded="lg"
                  resize="none"
                />
              </Field.Root>

              <Flex
                gap={3}
                justify="space-between"
                borderTop="1px solid"
                borderColor="gray.100"
                pt={5}
                direction={{ base: "column", sm: "row" }}
              >
                <Button variant="outline" colorPalette="verde" rounded="lg">
                  Reportar Problema
                </Button>
                <Button
                  colorPalette="verde"
                  bg="marca.primario"
                  rounded="lg"
                  flex="1"
                  onClick={() => navigate("/recolector/resultado")}
                >
                  <MdCheckCircleOutline /> Confirmar Operación
                </Button>
              </Flex>
            </VStack>
          </Box>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginadeValidacionMaterial;
