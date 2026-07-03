import { Box, HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaOferta from "../componentes/organismos/TarjetaOferta.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

// Ofertas de ejemplo alineadas al mockup "Ofertas Recibidas" (Entregable 3).
const OFERTAS = [
  {
    id: 1,
    nombreOfertante: "Reciclador Juan",
    tipoOfertante: "Reciclador",
    calificacion: "4.8",
    monto: "$4.80",
    distancia: "2km",
    observacion: "Recogida rápida, llego en 20 mins.",
  },
  {
    id: 2,
    nombreOfertante: "Centro EcoAcopio Sur",
    tipoOfertante: "Centro de Acopio",
    calificacion: "4.6",
    monto: "$6.20",
    distancia: "5km",
    observacion: "Podemos recoger mañana en la tarde.",
  },
];

const PaginadeReseniaOfertas = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : []}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      mostrarBuscador={false}
      anchoContenido="680px"
    >
      <Box
        bg="fondo.tarjeta"
        border="1px solid"
        borderColor="gray.200"
        borderRadius="xl"
        p={{ base: 5, md: 8 }}
      >
        <HStack gap={3} mb={6}>
          <Icono
            componente={
              <MdArrowBack cursor="pointer" onClick={() => navigate(-1)} aria-label="Volver" />
            }
            tamanio="xl"
          />
          <Text fontFamily="heading" fontWeight="700" fontSize="2xl">
            Ofertas Recibidas
          </Text>
        </HStack>

        <VStack align="stretch" gap={5}>
          {OFERTAS.map((oferta) => (
            <TarjetaOferta
              key={oferta.id}
              nombreOfertante={oferta.nombreOfertante}
              tipoOfertante={oferta.tipoOfertante}
              calificacion={oferta.calificacion}
              monto={oferta.monto}
              distancia={oferta.distancia}
              observacion={oferta.observacion}
              alAceptar={() => navigate(`${prefijoRuta}/publicacion-reservada`)}
              alRechazar={() => {}}
            />
          ))}
        </VStack>
      </Box>
    </DiseniodeAplicacion>
  );
};

export default PaginadeReseniaOfertas;
