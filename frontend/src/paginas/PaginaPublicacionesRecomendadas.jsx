import { Box, Flex, HStack, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineEventAvailable, MdOutlineInbox } from "react-icons/md";
import { useEffect, useRef, useState } from "react";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";
import TarjetaMaterialRecomendado from "../componentes/organismos/TarjetaMaterialRecomendado.jsx";
import TarjetaReservaVerificacion from "../componentes/organismos/TarjetaReservaVerificacion.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";
import { barrioMasCercano, etiquetaTipoResiduo } from "@/utilidades/barriosQuito";
import { obtenerEtapaReserva } from "@/utilidades/progresoReserva";
import { obtenerMisReservas } from "@/servicios/publicacionService";
import { useEmparejamiento } from "@/utilidades/useEmparejamiento";
import { usePublicaciones } from "@/utilidades/usePublicaciones";
import { useReservarPublicacion } from "@/utilidades/useReservarPublicacion";

/**
 * Publicaciones recomendadas para compradores (reciclador y centro), cargadas
 * usando el motor de emparejamiento para ordenar según distancia y precio.
 * Incluye el panel de Reservas con su estado de verificación; la reserva
 * abierta se conserva en sessionStorage para que al volver desde la vista de
 * la ruta el usuario continúe en la misma publicación.
 */
const PaginaPublicacionesRecomendadas = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const esCentro = rol === "centro";
  const claveReservaAbierta = `reserva-abierta-${rol}`;

  const { publicaciones, cargando, mensajeError, buscar, cargarInicial } = useEmparejamiento();
  const { publicaciones: reservas, cargando: cargandoReservas } =
    usePublicaciones(obtenerMisReservas);
  const { reservar, reservandoId } = useReservarPublicacion(rol);
  const busquedaInicialRef = useRef(false);
  const [reservaAbiertaId, setReservaAbiertaId] = useState(() =>
    sessionStorage.getItem(claveReservaAbierta),
  );

  useEffect(() => {
    if (!busquedaInicialRef.current) {
      busquedaInicialRef.current = true;
      cargarInicial();
    }
  }, [cargarInicial]);

  const reservasActivas = reservas.filter((reserva) => reserva.estado !== "FINALIZADA");

  // La reserva abierta solo cuenta si sigue activa; si fue finalizada o
  // liberada, el contexto guardado se descarta de sessionStorage.
  const reservaAbiertaValida = reservasActivas.some(
    (reserva) => String(reserva.publicacionId) === reservaAbiertaId,
  );

  useEffect(() => {
    if (cargandoReservas || !reservaAbiertaId || reservaAbiertaValida) return;
    sessionStorage.removeItem(claveReservaAbierta);
  }, [cargandoReservas, reservaAbiertaId, reservaAbiertaValida, claveReservaAbierta]);

  const abrirReserva = (publicacionId) => {
    sessionStorage.setItem(claveReservaAbierta, String(publicacionId));
    setReservaAbiertaId(String(publicacionId));
  };

  const cerrarReserva = () => {
    sessionStorage.removeItem(claveReservaAbierta);
    setReservaAbiertaId(null);
  };

  const manejarBusqueda = (filtros) => {
    buscar(filtros);
  };

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : SUBTITULO_RECOLECTOR}
      rutaNuevaPublicacion={esCentro ? undefined : "/recolector/vender/crear-publicacion"}
      anchoContenido="1160px"
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

        <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
          {/* Publicaciones recomendadas */}
          <VStack align="stretch" gap={6} flex="1" minW={0} w="100%">
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
                    telefonoCreador={publicacion.telefonoCreador}
                    alVerDetalle={() => navigate(`/${rol}/detalle/${publicacion.publicacionId}`)}
                    alReservar={() => reservar(publicacion.publicacionId)}
                    reservando={reservandoId === publicacion.publicacionId}
                  />
                ))}
              </VStack>
            )}
          </VStack>

          {/* Reservas con su estado de verificación */}
          <Box
            w={{ base: "100%", lg: "320px" }}
            flexShrink={0}
            bg="fondo.cabeceraTarjeta"
            border="1px solid"
            borderColor="gray.200"
            borderRadius="xl"
            p={4}
          >
            <HStack gap={2} mb={4}>
              <Icono
                componente={<MdOutlineEventAvailable />}
                tamanio="lg"
                color="marca.secundario"
              />
              <Text fontFamily="heading" fontWeight="700">
                Reservas
              </Text>
            </HStack>
            {cargandoReservas ? (
              <Flex justify="center" py={6}>
                <Spinner color="marca.primario" />
              </Flex>
            ) : reservasActivas.length === 0 ? (
              <VStack
                border="1px dashed"
                borderColor="gray.300"
                borderRadius="lg"
                py={8}
                px={3}
                gap={2}
                textAlign="center"
                bg="fondo.tarjeta"
              >
                <Icono componente={<MdOutlineInbox />} tamanio="2xl" color="gray.400" />
                <Text fontSize="sm" fontWeight="600">
                  Aún no tienes reservas
                </Text>
                <Text fontSize="xs" color="gray.600">
                  Cuando reserves un material aparecerá aquí con su estado de verificación.
                </Text>
              </VStack>
            ) : (
              <VStack align="stretch" gap={3}>
                {reservasActivas.map((reserva) => (
                  <TarjetaReservaVerificacion
                    key={reserva.publicacionId}
                    reserva={reserva}
                    etapa={obtenerEtapaReserva(reserva.publicacionId)}
                    abierta={String(reserva.publicacionId) === reservaAbiertaId}
                    alAbrir={() => abrirReserva(reserva.publicacionId)}
                    alCerrar={cerrarReserva}
                    alVerRuta={
                      esCentro ? undefined : () => navigate("/recolector/ruta-recoleccion")
                    }
                    alCoordinar={() => navigate(`/${rol}/coordinar/${reserva.publicacionId}`)}
                    alVerificar={() => navigate(`/${rol}/verificar/${reserva.publicacionId}`)}
                  />
                ))}
              </VStack>
            )}
          </Box>
        </Flex>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionesRecomendadas;
