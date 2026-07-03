import { HStack, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import DetallePublicacion from "../componentes/organismos/DetallePublicacion";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";

const PaginaDetallePublicacion = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const esCentro = rol === "centro";

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : SUBTITULO_RECOLECTOR}
      mostrarBuscador={false}
      anchoContenido="1160px"
    >
      <VStack align="stretch" gap={6}>
        <HStack gap={3}>
          <Icono
            componente={
              <MdArrowBack cursor="pointer" onClick={() => navigate(-1)} aria-label="Volver" />
            }
            tamanio="xl"
          />
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Detalle de Material
          </Text>
        </HStack>

        <DetallePublicacion
          vendedor={esCentro ? "Colectivo La Carolina" : "Maria L."}
          rotuloVendedor={esCentro ? "VENDEDOR (COLECTIVO)" : "VENDEDOR (CIUDADANO)"}
          detalleCalificacion={esCentro ? "(85 transacciones)" : "(120 operaciones)"}
          alRealizarOferta={() => navigate(`/${rol}/realizar-oferta/${id ?? "1"}`)}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaDetallePublicacion;
