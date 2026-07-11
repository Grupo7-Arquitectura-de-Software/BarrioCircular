import { useEffect, useState } from "react";
import { Box, Button, Flex, Text } from "@chakra-ui/react";
import { MdOutlineMyLocation } from "react-icons/md";
import { MapContainer, Marker, TileLayer, useMap, useMapEvents } from "react-leaflet";
import L from "leaflet";

import { toaster } from "@/components/ui/toaster-instance";
import { barrioMasCercano, estaDentroDeQuito } from "@/utilidades/barriosQuito";

// Centro por defecto del mapa: Quito (La Carolina).
const CENTRO_QUITO = [-0.180653, -78.467838];

const crearIconoUbicacion = () =>
  L.divIcon({
    html: `<div style="display:flex;align-items:center;justify-content:center;width:34px;height:34px;border-radius:50% 50% 50% 0;transform:rotate(-45deg);background:#006a3a;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);"><div style="width:10px;height:10px;border-radius:50%;background:#fff;"></div></div>`,
    className: "leaflet-div-icon",
    iconSize: [34, 34],
    iconAnchor: [17, 34],
  });

// Registra los clics del mapa para colocar el marcador de recogida.
const CapturadorDeClics = ({ alSeleccionar }) => {
  useMapEvents({
    click: (evento) => alSeleccionar(evento.latlng.lat, evento.latlng.lng),
  });
  return null;
};

// Recentra el mapa cuando la ubicación cambia desde fuera (barrio del
// desplegable o geolocalización del navegador).
const CentrarMapa = ({ latitud, longitud }) => {
  const mapa = useMap();

  useEffect(() => {
    if (latitud != null && longitud != null) {
      mapa.setView([latitud, longitud], Math.max(mapa.getZoom(), 14));
    }
  }, [mapa, latitud, longitud]);

  return null;
};

/**
 * Selector de ubicación sobre mapa (Leaflet): permite marcar el punto de
 * recogida con un clic o usar la ubicación actual del navegador. Valida que
 * las coordenadas caigan dentro del área de Quito que acepta el backend.
 * `valor`: { latitud, longitud } | null. `alCambiar(latitud, longitud)`.
 */
const SelectorUbicacionMapa = ({ valor, alCambiar }) => {
  const [obteniendoUbicacion, setObteniendoUbicacion] = useState(false);
  const posicion = valor ? [Number(valor.latitud), Number(valor.longitud)] : null;

  const seleccionar = (latitud, longitud) => {
    if (!estaDentroDeQuito(latitud, longitud)) {
      toaster.create({
        title: "Ubicación fuera de Quito",
        description: "Selecciona un punto dentro del área de Quito para publicar el material.",
        type: "warning",
        duration: 4000,
      });
      return;
    }
    alCambiar(latitud, longitud);
  };

  const usarUbicacionActual = () => {
    if (!navigator.geolocation) {
      toaster.create({
        title: "Geolocalización no disponible",
        description: "Tu navegador no permite obtener la ubicación actual.",
        type: "error",
        duration: 4000,
      });
      return;
    }

    setObteniendoUbicacion(true);
    navigator.geolocation.getCurrentPosition(
      (resultado) => {
        seleccionar(resultado.coords.latitude, resultado.coords.longitude);
        setObteniendoUbicacion(false);
      },
      () => {
        toaster.create({
          title: "No se pudo obtener tu ubicación",
          description: "Revisa el permiso de ubicación del navegador e intenta de nuevo.",
          type: "error",
          duration: 4500,
        });
        setObteniendoUbicacion(false);
      },
      { enableHighAccuracy: true, timeout: 10000 },
    );
  };

  return (
    <Box w="100%">
      <Box
        h={{ base: "260px", md: "320px" }}
        borderRadius="lg"
        overflow="hidden"
        border="1px solid"
        borderColor="gray.200"
      >
        <MapContainer
          center={posicion || CENTRO_QUITO}
          zoom={12}
          scrollWheelZoom={false}
          style={{ height: "100%", width: "100%" }}
        >
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          />
          <CapturadorDeClics alSeleccionar={seleccionar} />
          <CentrarMapa latitud={posicion?.[0]} longitud={posicion?.[1]} />
          {posicion && <Marker position={posicion} icon={crearIconoUbicacion()} />}
        </MapContainer>
      </Box>

      <Flex
        mt={3}
        gap={3}
        align={{ base: "stretch", sm: "center" }}
        justify="space-between"
        direction={{ base: "column", sm: "row" }}
      >
        <Text fontSize="sm" color={posicion ? "gray.700" : "gray.500"}>
          {posicion
            ? `Punto de recogida: ${barrioMasCercano(posicion[0], posicion[1])} (${posicion[0].toFixed(5)}, ${posicion[1].toFixed(5)})`
            : "Toca el mapa para marcar el punto de recogida."}
        </Text>
        <Button
          type="button"
          size="sm"
          variant="outline"
          colorPalette="verde"
          rounded="lg"
          flexShrink={0}
          loading={obteniendoUbicacion}
          loadingText="Ubicando"
          onClick={usarUbicacionActual}
        >
          <MdOutlineMyLocation /> Usar mi ubicación actual
        </Button>
      </Flex>
    </Box>
  );
};

export default SelectorUbicacionMapa;
