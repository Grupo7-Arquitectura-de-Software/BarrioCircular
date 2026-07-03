import { Box, HStack, Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaOferta from "../componentes/organismos/TarjetaOferta.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

/**
 * Confirmación de la mejor oferta antes de coordinar (mockup "Aceptar Oferta").
 */
const PaginaPublicacionReservada = ({ prefijoRuta = "/ciudadano" }) => {
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
            Aceptar mejor oferta
          </Text>
        </HStack>

        <TarjetaOferta
          nombreOfertante="Reciclador Juan"
          tipoOfertante="Reciclador"
          calificacion="4.8"
          monto="$4.80"
          distancia="2km"
          observacion="Recogida rápida, llego en 20 mins."
          alAceptar={() => navigate(`${prefijoRuta}/coordinar`)}
          alRechazar={() => navigate(-1)}
        />
      </Box>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionReservada;
