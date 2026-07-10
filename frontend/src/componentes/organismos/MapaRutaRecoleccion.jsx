import { Box, Text } from "@chakra-ui/react";
import { MapContainer, Marker, Polyline, Popup, TileLayer } from "react-leaflet";
import L from "leaflet";
import MarcadorParada from "@/componentes/moleculas/MarcadorParada.jsx";

const crearIconoOrigen = () =>
  L.divIcon({
    html: `<div style="display:flex;align-items:center;justify-content:center;width:38px;height:38px;border-radius:50%;background:#F59E0B;color:#fff;border:2px solid #fff;box-shadow:0 0 0 3px rgba(245,158,11,0.25);font-weight:700;font-size:0.95rem;">R</div>`,
    className: "leaflet-div-icon",
    iconSize: [38, 38],
    iconAnchor: [19, 38],
    popupAnchor: [0, -38],
  });

const obtenerCoordenadas = (elemento) =>
  elemento && elemento.latitud != null && elemento.longitud != null
    ? [Number(elemento.latitud), Number(elemento.longitud)]
    : null;

const MapaRutaRecoleccion = ({ origen, paradas = [] }) => {
  const coordenadasOrigen = obtenerCoordenadas(origen);
  const paradasOrdenadas = [...paradas].sort((a, b) => Number(a.orden ?? 0) - Number(b.orden ?? 0));
  const puntosRuta = paradasOrdenadas
    .map((parada) => obtenerCoordenadas(parada))
    .filter(Boolean);

  const centro = coordenadasOrigen || puntosRuta[0] || [0, 0];

  return (
    <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={5}>
      <Text fontFamily="heading" fontWeight="700" fontSize="xl" mb={4}>
        Ruta de hoy
      </Text>

      <Box h={{ base: "320px", md: "440px" }} minH="320px" borderRadius="xl" overflow="hidden">
        <MapContainer center={centro} zoom={14} scrollWheelZoom={false} style={{ height: "100%", width: "100%" }}>
          <TileLayer
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          />

          {coordenadasOrigen && (
            <Marker position={coordenadasOrigen} icon={crearIconoOrigen()}>
              <Popup>Origen reciclador</Popup>
            </Marker>
          )}

          {paradasOrdenadas.map((parada) => (
            <MarcadorParada key={parada.paradaId ?? parada.orden} parada={parada} />
          ))}

          {coordenadasOrigen && puntosRuta.length > 0 && (
            <Polyline positions={[coordenadasOrigen, ...puntosRuta]} pathOptions={{ color: "#2563EB", weight: 4 }} />
          )}
        </MapContainer>
      </Box>
    </Box>
  );
};

export default MapaRutaRecoleccion;
