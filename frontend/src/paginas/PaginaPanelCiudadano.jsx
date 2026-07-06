import { Button, Flex, SimpleGrid, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdAdd, MdOutlineInventory2, MdOutlinePayments, MdOutlineRecycling } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaEstadistica from "../componentes/moleculas/TarjetaEstadistica.jsx";
import TarjetaPublicacion from "../componentes/organismos/TarjetaPublicacion.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";
import {
  barrioMasCercano,
  etiquetaEstadoPublicacion,
  etiquetaTipoResiduo,
} from "@/utilidades/barriosQuito";
import { obtenerMisPublicaciones } from "@/servicios/publicacionService";
import { usePublicaciones } from "@/utilidades/usePublicaciones";

const PaginaPanelCiudadano = () => {
  const navigate = useNavigate();
  const { publicaciones, cargando, mensajeError } = usePublicaciones(obtenerMisPublicaciones);

  const totalKgPublicados = publicaciones.reduce((suma, publicacion) => {
    return publicacion.estado === "FINALIZADA" ? suma + publicacion.pesoKg : suma;
  }, 0);
  const anunciosActivos = publicaciones.filter(
    (publicacion) => publicacion.estado === "DISPONIBLE" || publicacion.estado === "RESERVADA",
  ).length;
  const valorPublicado = publicaciones.reduce(
    (suma, publicacion) => suma + publicacion.pesoKg * Number(publicacion.precioPorKilo),
    0,
  );
  const recientes = publicaciones.slice(0, 3);

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CIUDADANO}
      rutaNuevaPublicacion={RUTA_NUEVA_PUBLICACION_CIUDADANO}
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Mi Panel
          </Text>
          <Text color="gray.600">Rastrea tu impacto regenerativo y anuncios activos.</Text>
        </VStack>

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : mensajeError ? (
          <Text color="marca.error">{mensajeError}</Text>
        ) : (
          <>
            {/* Estadísticas calculadas desde las publicaciones reales */}
            <SimpleGrid columns={{ base: 1, md: 3 }} gap={5}>
              <TarjetaEstadistica
                icono={<MdOutlineRecycling />}
                etiqueta="Total Reciclado"
                valor={String(totalKgPublicados)}
                unidad="kg"
                acento="verde"
              />
              <TarjetaEstadistica
                icono={<MdOutlinePayments />}
                etiqueta="Valor Publicado"
                valor={`$${valorPublicado.toFixed(2)}`}
                acento="azul"
              />
              <TarjetaEstadistica
                icono={<MdOutlineInventory2 />}
                etiqueta="Anuncios Activos"
                valor={String(anunciosActivos)}
                acento="neutro"
                etiquetaAccion="Ver todo"
                alAccionar={() => navigate("/ciudadano/publicacion-disponible")}
              />
            </SimpleGrid>

            {/* Publicaciones recientes */}
            <VStack align="stretch" gap={4}>
              <Flex justify="space-between" align="center">
                <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                  Publicaciones Recientes
                </Text>
              </Flex>

              {recientes.length === 0 ? (
                <VStack
                  border="1px dashed"
                  borderColor="gray.300"
                  borderRadius="xl"
                  py={10}
                  gap={3}
                  textAlign="center"
                >
                  <Text fontWeight="600">Aún no tienes publicaciones</Text>
                  <Button
                    colorPalette="verde"
                    bg="marca.primario"
                    rounded="lg"
                    onClick={() => navigate(RUTA_NUEVA_PUBLICACION_CIUDADANO)}
                  >
                    <MdAdd /> Nueva Publicación
                  </Button>
                </VStack>
              ) : (
                <SimpleGrid columns={{ base: 1, md: 2, xl: 3 }} gap={5}>
                  {recientes.map((publicacion) => (
                    <TarjetaPublicacion
                      key={publicacion.publicacionId}
                      titulo={`${publicacion.pesoKg}kg de ${etiquetaTipoResiduo(publicacion.tipoResiduo)}`}
                      descripcion={`$${Number(publicacion.precioPorKilo).toFixed(2)} por kilo`}
                      pesoKg={publicacion.pesoKg}
                      ubicacion={barrioMasCercano(publicacion.latitud, publicacion.longitud)}
                      estado={etiquetaEstadoPublicacion(publicacion.estado)}
                      imagenUrl={publicacion.evidenciaUrl}
                      alHacerClick={() => navigate("/ciudadano/publicacion-disponible")}
                    />
                  ))}
                </SimpleGrid>
              )}
            </VStack>
          </>
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPanelCiudadano;
