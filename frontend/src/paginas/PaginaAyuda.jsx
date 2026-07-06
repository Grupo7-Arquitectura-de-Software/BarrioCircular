import { Accordion, Box, HStack, Span, Text, VStack } from "@chakra-ui/react";
import { MdOutlineHelpOutline, MdOutlineMail } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";

const PREGUNTAS_GENERALES = [
  {
    valor: "publicar",
    pregunta: "¿Cómo publico un material reciclable?",
    respuesta:
      "Usa el botón «Nueva Publicación», elige el tipo de material (PET, cartón, vidrio o chatarra), indica el peso estimado, el precio por kilo, el barrio de recogida y sube una foto del material como evidencia.",
  },
  {
    valor: "reservas",
    pregunta: "¿Cómo funciona la reserva?",
    respuesta:
      "Los recicladores y centros de acopio revisan las publicaciones disponibles y reservan el material que les interesa al precio publicado. Al reservarse, la publicación deja de estar disponible para otros compradores y se pasa a coordinar la entrega.",
  },
  {
    valor: "roles",
    pregunta: "¿Qué puede hacer cada rol?",
    respuesta:
      "El Ciudadano publica materiales y gestiona sus anuncios. El Reciclador compra materiales y también puede vender los que recolecta. El Centro de Recolección compra a gran escala y coordina la recepción de materiales.",
  },
  {
    valor: "coordinacion",
    pregunta: "¿Cómo se coordina la entrega?",
    respuesta:
      "Cuando un material es reservado, comprador y vendedor coordinan el punto y la hora de recogida. Al entregar el material se verifica el peso y estado, y la operación se confirma en la plataforma.",
  },
];

const DISENIO_POR_ROL = {
  ciudadano: {
    navegacion: NAVEGACION_CIUDADANO,
    rutaNuevaPublicacion: RUTA_NUEVA_PUBLICACION_CIUDADANO,
  },
  recolector: {
    navegacion: NAVEGACION_RECOLECTOR,
    subtituloMarca: SUBTITULO_RECOLECTOR,
    rutaNuevaPublicacion: "/recolector/vender/crear-publicacion",
  },
  centro: {
    navegacion: NAVEGACION_CENTRO,
    subtituloMarca: SUBTITULO_CENTRO,
  },
};

/**
 * Centro de ayuda compartido por los tres roles: preguntas frecuentes
 * sobre el flujo de la economía circular y contacto de soporte.
 */
const PaginaAyuda = ({ rol = "ciudadano" }) => {
  const disenio = DISENIO_POR_ROL[rol] || DISENIO_POR_ROL.ciudadano;

  return (
    <DiseniodeAplicacion
      navegacion={disenio.navegacion}
      subtituloMarca={disenio.subtituloMarca}
      rutaNuevaPublicacion={disenio.rutaNuevaPublicacion}
      mostrarBuscador={false}
      anchoContenido="760px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <HStack gap={2}>
            <Icono componente={<MdOutlineHelpOutline />} tamanio="2xl" color="marca.primario" />
            <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
              Centro de Ayuda
            </Text>
          </HStack>
          <Text color="gray.600">Preguntas frecuentes sobre cómo funciona BarrioCircular.</Text>
        </VStack>

        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={4}>
          <Accordion.Root collapsible defaultValue={["publicar"]}>
            {PREGUNTAS_GENERALES.map((item) => (
              <Accordion.Item key={item.valor} value={item.valor}>
                <Accordion.ItemTrigger>
                  <Span flex="1" fontWeight="600" textAlign="left">
                    {item.pregunta}
                  </Span>
                  <Accordion.ItemIndicator />
                </Accordion.ItemTrigger>
                <Accordion.ItemContent>
                  <Accordion.ItemBody color="gray.600" fontSize="sm">
                    {item.respuesta}
                  </Accordion.ItemBody>
                </Accordion.ItemContent>
              </Accordion.Item>
            ))}
          </Accordion.Root>
        </Box>

        <HStack
          bg="fondo.cabeceraTarjeta"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="xl"
          px={5}
          py={4}
          gap={3}
        >
          <Icono componente={<MdOutlineMail />} tamanio="xl" color="marca.secundario" />
          <Box>
            <Text fontWeight="600" fontSize="sm">
              ¿Necesitas más ayuda?
            </Text>
            <Text fontSize="sm" color="gray.600">
              Escríbenos a soporte@barriocircular.ec y te responderemos lo antes posible.
            </Text>
          </Box>
        </HStack>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaAyuda;
