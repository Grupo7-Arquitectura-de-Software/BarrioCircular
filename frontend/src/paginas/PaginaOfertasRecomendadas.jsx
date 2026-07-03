import { Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";
import { MATERIALES_RECOMENDADOS } from "@/utilidades/datosMercado";

/**
 * Mercado de materiales recomendados (mockup "Ofertas Recomendadas"),
 * compartido por el reciclador y el centro de recolección.
 */
const PaginaOfertasRecomendadas = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const esCentro = rol === "centro";

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : SUBTITULO_RECOLECTOR}
      rutaNuevaPublicacion={esCentro ? undefined : "/recolector/vender/crear-publicacion"}
      anchoContenido="960px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text
            fontFamily="heading"
            fontWeight="700"
            fontSize={{ base: "2xl", md: "3xl" }}
            color="marca.primario"
          >
            Ofertas Recomendadas
          </Text>
          <Text color="gray.600">
            Materiales reciclables de alta calidad verificados por la comunidad, cerca de ti.
          </Text>
        </VStack>

        <FormularioBuscarMateriales />

        <VStack align="stretch" gap={4}>
          {MATERIALES_RECOMENDADOS.map((material) => (
            <TarjetaMaterialRecomendado
              key={material.id}
              titulo={material.titulo}
              precioPorKilo={material.precioPorKilo}
              pesoKg={material.pesoKg}
              ubicacion={material.ubicacion}
              distanciaKm={material.distanciaKm}
              descripcion={material.descripcion}
              puntuacion={material.puntuacion}
              alVerDetalle={() => navigate(`/${rol}/detalle/${material.id}`)}
              alHacerOferta={() => navigate(`/${rol}/realizar-oferta/${material.id}`)}
            />
          ))}
        </VStack>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaOfertasRecomendadas;
