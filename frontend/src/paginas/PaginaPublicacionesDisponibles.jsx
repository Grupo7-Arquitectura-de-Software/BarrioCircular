import { Button, Flex, SimpleGrid, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdAdd, MdOutlineInbox } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaPublicacion from "../componentes/organismos/TarjetaPublicacion.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";
import {
  barrioMasCercano,
  etiquetaEstadoPublicacion,
  etiquetaTipoResiduo,
} from "@/utilidades/barriosQuito";
import { obtenerMisPublicaciones } from "@/servicios/publicacionService";
import { usePublicaciones } from "@/utilidades/usePublicaciones";
import { useFinalizarPublicacion } from "@/utilidades/useFinalizarPublicacion";

/**
 * Mis Publicaciones: lista real de las publicaciones del usuario autenticado
 * (GET /api/publicaciones/mias), compartida por ciudadano y reciclador.
 * Las publicaciones FINALIZADA se muestran aparte, en el Historial.
 */
const PaginaPublicacionesDisponibles = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const esCiudadano = prefijoRuta === "/ciudadano";
  const { publicaciones, setPublicaciones, cargando, mensajeError } =
    usePublicaciones(obtenerMisPublicaciones);
  const { finalizar, finalizandoId } = useFinalizarPublicacion();

  const publicacionesActivas = publicaciones.filter((p) => p.estado !== "FINALIZADA");
  const historial = publicaciones.filter((p) => p.estado === "FINALIZADA");

  const finalizarPublicacionActiva = (publicacionId) =>
    finalizar(publicacionId, (resultado) =>
      setPublicaciones((actuales) =>
        actuales.map((p) => (p.publicacionId === publicacionId ? resultado : p)),
      ),
    );

  const rutaCrear = `${prefijoRuta}/crear-publicacion`;

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : NAVEGACION_RECOLECTOR}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Mis Publicaciones
          </Text>
          <Text color="gray.600">
            Los materiales que has publicado y el estado de cada anuncio.
          </Text>
        </VStack>

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
            px={6}
            gap={3}
            textAlign="center"
          >
            <Icono componente={<MdOutlineInbox />} tamanio="4xl" color="gray.400" />
            <Text fontWeight="600" fontSize="lg">
              Aún no tienes publicaciones
            </Text>
            <Text fontSize="sm" color="gray.600" maxW="380px">
              Publica tu primer material reciclable para que los recicladores y centros de acopio
              puedan reservarlos.
            </Text>
            <Button
              colorPalette="verde"
              bg="marca.primario"
              rounded="lg"
              mt={2}
              onClick={() => navigate(rutaCrear)}
            >
              <MdAdd /> Nueva Publicación
            </Button>
          </VStack>
        ) : (
          <>
            {publicacionesActivas.length === 0 ? (
              <Text color="gray.600">No tienes publicaciones activas por ahora.</Text>
            ) : (
              <SimpleGrid columns={{ base: 1, md: 2, xl: 3 }} gap={5}>
                {publicacionesActivas.map((publicacion) => (
                  <TarjetaPublicacion
                    key={publicacion.publicacionId}
                    titulo={`${publicacion.pesoKg}kg de ${etiquetaTipoResiduo(publicacion.tipoResiduo)}`}
                    descripcion={`$${Number(publicacion.precioPorKilo).toFixed(2)} por kilo`}
                    pesoKg={publicacion.pesoKg}
                    ubicacion={barrioMasCercano(publicacion.latitud, publicacion.longitud)}
                    estado={etiquetaEstadoPublicacion(publicacion.estado)}
                    imagenUrl={publicacion.evidenciaUrl}
                    etiquetaAccion={publicacion.estado === "RESERVADA" ? "Finalizar" : undefined}
                    accionando={finalizandoId === publicacion.publicacionId}
                    alAccionar={() => finalizarPublicacionActiva(publicacion.publicacionId)}
                  />
                ))}
              </SimpleGrid>
            )}

            {historial.length > 0 && (
              <VStack align="stretch" gap={4} mt={2}>
                <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                  Historial
                </Text>
                <SimpleGrid columns={{ base: 1, md: 2, xl: 3 }} gap={5}>
                  {historial.map((publicacion) => (
                    <TarjetaPublicacion
                      key={publicacion.publicacionId}
                      titulo={`${publicacion.pesoKg}kg de ${etiquetaTipoResiduo(publicacion.tipoResiduo)}`}
                      descripcion={`$${Number(publicacion.precioPorKilo).toFixed(2)} por kilo`}
                      pesoKg={publicacion.pesoKg}
                      ubicacion={barrioMasCercano(publicacion.latitud, publicacion.longitud)}
                      estado={etiquetaEstadoPublicacion(publicacion.estado)}
                      imagenUrl={publicacion.evidenciaUrl}
                    />
                  ))}
                </SimpleGrid>
              </VStack>
            )}
          </>
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionesDisponibles;
