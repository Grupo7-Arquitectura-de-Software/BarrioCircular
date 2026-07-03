import { Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioCrearPublicacion from "../componentes/organismos/FormularioCrearPublicacion";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

const PaginaCrearPublicaciones = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : NAVEGACION_RECOLECTOR}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      mostrarBuscador={false}
      anchoContenido="760px"
      mostrarAtras={!esCiudadano}
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Nueva Publicación
          </Text>
          <Text color="gray.600">
            Contribuye a la economía circular publicando materiales disponibles en tu área.
          </Text>
        </VStack>

        <FormularioCrearPublicacion
          alPublicar={() => navigate(`${prefijoRuta}/publicacion-disponible`)}
          alCancelar={() => navigate(-1)}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCrearPublicaciones;
