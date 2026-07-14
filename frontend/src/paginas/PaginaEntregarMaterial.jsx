import { Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioEntregaMaterial from "../componentes/organismos/FormularioEntregaMaterial";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";

const PaginaEntregarMaterial = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCiudadano ? undefined : SUBTITULO_RECOLECTOR}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      mostrarBuscador={false}
      anchoContenido="640px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Entregar Material
          </Text>
          <Text color="gray.600">
            Confirma la entrega del material para completar la operación.
          </Text>
        </VStack>

        <FormularioEntregaMaterial alConfirmar={() => navigate(`${prefijoRuta}/resultado`)} />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaEntregarMaterial;
