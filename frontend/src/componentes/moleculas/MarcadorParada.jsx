import { Marker, Popup } from "react-leaflet";
import L from "leaflet";

const ESTADOS_PARADA = {
  COMPLETADA: {
    etiqueta: "Completada",
    color: "#16A34A",
  },
  EN_CURSO: {
    etiqueta: "En curso",
    color: "#2563EB",
  },
  PENDIENTE: {
    etiqueta: "Pendiente",
    color: "#6B7280",
  },
};

const obtenerConfiguracionEstado = (estado) =>
  ESTADOS_PARADA[estado] || {
    etiqueta: estado || "Sin estado",
    color: "#6B7280",
  };

const crearIconoParada = (orden, color) =>
  L.divIcon({
    html: `<div style="display:flex;align-items:center;justify-content:center;width:36px;height:36px;border-radius:50%;background:${color};color:#fff;border:2px solid #ffffff;box-shadow:0 0 0 2px rgba(255,255,255,0.7);font-weight:700;font-size:0.9rem;">${orden}</div>`,
    className: "leaflet-div-icon",
    iconSize: [36, 36],
    iconAnchor: [18, 36],
    popupAnchor: [0, -38],
  });

const MarcadorParada = ({ parada }) => {
  if (!parada || parada.latitud == null || parada.longitud == null) {
    return null;
  }

  const estado = obtenerConfiguracionEstado(parada.estado);
  const posicion = [Number(parada.latitud), Number(parada.longitud)];
  const icono = crearIconoParada(parada.orden ?? "?", estado.color);

  return (
    <Marker position={posicion} icon={icono}>
      <Popup>
        <div style={{ minWidth: 180 }}>
          <strong>Parada {parada.orden}</strong>
          <div>{parada.publicacionId ? `Publicación ${parada.publicacionId}` : "Sin publicación"}</div>
          <div>{estado.etiqueta}</div>
          <div>{parada.tipoResiduo ? `Material: ${parada.tipoResiduo}` : "Material no disponible"}</div>
          <div>{Number(parada.pesoKg || 0).toFixed(1)} kg</div>
        </div>
      </Popup>
    </Marker>
  );
};

export default MarcadorParada;
