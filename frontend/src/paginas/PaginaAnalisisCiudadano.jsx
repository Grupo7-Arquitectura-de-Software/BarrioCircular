import { Box, Flex, Progress, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { MdOutlineInventory2, MdOutlineRecycling, MdOutlineTrendingUp } from "react-icons/md";
import { LuLeaf } from "react-icons/lu";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaEstadistica from "../componentes/moleculas/TarjetaEstadistica.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

// Datos de ejemplo alineados a TipoResiduo; se reemplazarán por el API de análisis.
const RECICLAJE_POR_MATERIAL = [
  { material: "Cartón", kg: 120, porcentaje: 49 },
  { material: "Plástico PET", kg: 75, porcentaje: 31 },
  { material: "Vidrio", kg: 32, porcentaje: 13 },
  { material: "Chatarra", kg: 18, porcentaje: 7 },
];

const RECICLAJE_POR_MES = [
  { mes: "Febrero", kg: 28, porcentaje: 45 },
  { mes: "Marzo", kg: 35, porcentaje: 56 },
  { mes: "Abril", kg: 41, porcentaje: 66 },
  { mes: "Mayo", kg: 48, porcentaje: 77 },
  { mes: "Junio", kg: 62, porcentaje: 100 },
  { mes: "Julio", kg: 31, porcentaje: 50 },
];

const TarjetaDesglose = ({ titulo, filas, colorPalette }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
    <Text fontFamily="heading" fontWeight="700" fontSize="lg" mb={5}>
      {titulo}
    </Text>
    <VStack align="stretch" gap={4}>
      {filas.map((fila) => (
        <Box key={fila.etiqueta}>
          <Flex justify="space-between" mb={1}>
            <Text fontSize="sm" fontWeight="600">
              {fila.etiqueta}
            </Text>
            <Text fontSize="sm" color="gray.600">
              {fila.kg} kg
            </Text>
          </Flex>
          <Progress.Root value={fila.porcentaje} size="sm" colorPalette={colorPalette}>
            <Progress.Track borderRadius="full">
              <Progress.Range />
            </Progress.Track>
          </Progress.Root>
        </Box>
      ))}
    </VStack>
  </Box>
);

/**
 * Análisis del impacto del ciudadano: totales y desgloses de reciclaje
 * por material y por mes.
 */
const PaginaAnalisisCiudadano = () => {
  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CIUDADANO}
      rutaNuevaPublicacion={RUTA_NUEVA_PUBLICACION_CIUDADANO}
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Análisis
          </Text>
          <Text color="gray.600">Tu impacto regenerativo en la comunidad, en números.</Text>
        </VStack>

        <SimpleGrid columns={{ base: 1, md: 3 }} gap={5}>
          <TarjetaEstadistica
            icono={<MdOutlineRecycling />}
            etiqueta="Total Reciclado"
            valor="245"
            unidad="kg"
            acento="verde"
            insignia="+12% este mes"
          />
          <TarjetaEstadistica
            icono={<LuLeaf />}
            etiqueta="Compensación de CO2"
            valor="1.2"
            unidad="toneladas"
            acento="azul"
          />
          <TarjetaEstadistica
            icono={<MdOutlineTrendingUp />}
            etiqueta="Ingresos Generados"
            valor="$86.50"
            acento="neutro"
          />
        </SimpleGrid>

        <SimpleGrid columns={{ base: 1, lg: 2 }} gap={6}>
          <TarjetaDesglose
            titulo="Reciclaje por material"
            filas={RECICLAJE_POR_MATERIAL.map((fila) => ({
              etiqueta: fila.material,
              kg: fila.kg,
              porcentaje: fila.porcentaje,
            }))}
            colorPalette="verde"
          />
          <TarjetaDesglose
            titulo="Reciclaje por mes"
            filas={RECICLAJE_POR_MES.map((fila) => ({
              etiqueta: fila.mes,
              kg: fila.kg,
              porcentaje: fila.porcentaje,
            }))}
            colorPalette="azul"
          />
        </SimpleGrid>

        <Box bg="verde.50" border="1px solid" borderColor="verde.200" borderRadius="xl" p={5}>
          <Flex gap={3} align="center">
            <MdOutlineInventory2 size={22} color="var(--chakra-colors-verde-600)" />
            <Text fontSize="sm" color="gray.700">
              Has desviado <b>245 kg</b> de residuos del vertedero este año. ¡Equivale a llenar 8
              contenedores domésticos!
            </Text>
          </Flex>
        </Box>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaAnalisisCiudadano;
