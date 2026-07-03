import { HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioRealizarOferta from "../componentes/organismos/FormularioRealizarOferta";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";

const PaginaRealizarOferta = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const esCentro = rol === "centro";

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : SUBTITULO_RECOLECTOR}
      mostrarBuscador={false}
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <HStack gap={3}>
            <Icono
              componente={
                <MdArrowBack cursor="pointer" onClick={() => navigate(-1)} aria-label="Volver" />
              }
              tamanio="xl"
            />
            <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
              Enviar Oferta
            </Text>
          </HStack>
          <Text color="gray.600" pl={9}>
            Propón tu mejor precio para adquirir este material reciclable.
          </Text>
        </VStack>

        <FormularioRealizarOferta
          vendedor={esCentro ? "Empresa Plástica S.A." : "Maria L."}
          rotuloVendedor={esCentro ? "VENDEDOR (GENERADOR)" : "VENDEDOR (CIUDADANO)"}
          alEnviar={() => navigate(`/${rol}/espera/${id ?? "1"}`)}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaRealizarOferta;
