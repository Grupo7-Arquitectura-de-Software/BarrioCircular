import { Flex, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineInbox } from "react-icons/md";
import { useEffect, useRef } from "react";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";
import { barrioMasCercano, etiquetaTipoResiduo } from "@/utilidades/barriosQuito";
import { useEmparejamiento } from "@/utilidades/useEmparejamiento";
import { useReservarPublicacion } from "@/utilidades/useReservarPublicacion";

/**
 * Publicaciones recomendadas para compradores (reciclador y centro), cargadas
 * usando el motor de emparejamiento para ordenar según distancia y precio.
 */
const PaginaPublicacionesRecomendadas = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const esCentro = rol === "centro";

  const { publicaciones, cargando, mensajeError, buscar, cargarInicial } = useEmparejamiento();
  const { reservar, reservandoId } = useReservarPublicacion(rol);
  const busquedaInicialRef = useRef(false);

  useEffect(() => {
    if (!busquedaInicialRef.current) {
      busquedaInicialRef.current = true;
      cargarInicial();
    }
  }, [cargarInicial]);

  const manejarBusqueda = (filtros) => {
    buscar(filtros);
  };

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
            Publicaciones Recomendadas
          </Text>
          <Text color="gray.600">
            Materiales reciclables disponibles publicados por la comunidad. Resérvalos para
            coordinar su recolección.
          </Text>
        </VStack>

        <FormularioBuscarMateriales onBuscar={manejarBusqueda} />

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : mensajeError ? (
          <Text color="marca.error">{mensajeError}</Text>
        ) : publicaciones.length === 0 ? (
          <VStack
            border="1px dashed"
            borderColor="gray.300"
            borderRadius="xl"
            py={14}
            gap={3}
            textAlign="center"
          >
            <Icono componente={<MdOutlineInbox />} tamanio="4xl" color="gray.400" />
            <Text fontWeight="600" fontSize="lg">
              No hay materiales disponibles por ahora
            </Text>
            <Text fontSize="sm" color="gray.600">
              Prueba a cambiar los filtros de búsqueda o espera a que la comunidad publique más.
            </Text>
          </VStack>
        ) : (
          <VStack align="stretch" gap={4}>
            {publicaciones.map((publicacion) => (
              <TarjetaMaterialRecomendado
                key={publicacion.publicacionId}
                titulo={`${publicacion.pesoKg}kg de ${etiquetaTipoResiduo(publicacion.tipoResiduo)}`}
                precioPorKilo={`$${Number(publicacion.precioPorKilo).toFixed(2)}`}
                pesoKg={publicacion.pesoKg}
                ubicacion={barrioMasCercano(publicacion.latitud, publicacion.longitud)}
                descripcion={`Publicado el ${new Date(publicacion.fechaCreacion).toLocaleDateString()}`}
                imagenUrl={publicacion.evidenciaUrl}
                alVerDetalle={() => navigate(`/${rol}/detalle/${publicacion.publicacionId}`)}
                alReservar={() => reservar(publicacion.publicacionId)}
                reservando={reservandoId === publicacion.publicacionId}
              />
            ))}
          </VStack>
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionesRecomendadas;
