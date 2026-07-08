import { useState, useCallback } from "react";
import { useAuth } from "@clerk/clerk-react";
import { obtenerPublicacionesDisponibles } from "@/servicios/publicacionService";
import { buscarOfertasOptimas } from "@/servicios/emparejamientoService";
import { TIPOS_RESIDUO } from "@/utilidades/catalogoMateriales";

export const useEmparejamiento = () => {
  const { getToken } = useAuth();
  const [publicacionesOriginales, setPublicacionesOriginales] = useState([]);
  const [publicaciones, setPublicaciones] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [mensajeError, setMensajeError] = useState("");

  const cargarInicial = useCallback(async () => {
    setCargando(true);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
      const disponibles = await obtenerPublicacionesDisponibles(token);
      setPublicacionesOriginales(disponibles);
      setPublicaciones(disponibles);
    } catch (error) {
      setMensajeError(error.message || "No fue posible cargar las publicaciones.");
    } finally {
      setCargando(false);
    }
  }, [getToken]);

  const buscar = useCallback(
    async (filtros) => {
      setCargando(true);
      setMensajeError("");
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

        let disponibles = publicacionesOriginales;
        if (disponibles.length === 0) {
          disponibles = await obtenerPublicacionesDisponibles(token);
          setPublicacionesOriginales(disponibles);
        }

        const tiposMateriales =
          filtros.tipoMaterial && filtros.tipoMaterial !== "TODOS"
            ? [filtros.tipoMaterial]
            : TIPOS_RESIDUO.map((t) => t.value);

        const payload = {
          latitud: -0.180653, // Coordenadas por defecto (Quito)
          longitud: -78.467838,
          radioMaximoKm:
            filtros.distancia === "0" ? 50 : filtros.distancia ? Number(filtros.distancia) : 5,
          tiposMaterial: tiposMateriales,
          zonaDescriptiva: "Quito",
          pesoMinimo: filtros.pesoMinimo ? Number(filtros.pesoMinimo) : null,
          pesoMaximo: filtros.pesoMaximo ? Number(filtros.pesoMaximo) : null,
        };

        // Consumimos el endpoint de emparejamiento
        const resultadoEmparejamiento = await buscarOfertasOptimas(token, payload);
        const ofertasRecomendadas = resultadoEmparejamiento.ofertas || [];

        // Mapeamos y ordenamos en base a los resultados devueltos por el backend
        const publicacionesOrdenadas = ofertasRecomendadas
          .map((oferta) => disponibles.find((p) => p.publicacionId === oferta.publicacionId))
          .filter(Boolean); // Remover nulls

        setPublicaciones(publicacionesOrdenadas);
      } catch (error) {
        setMensajeError(error.message || "No fue posible realizar la búsqueda de materiales.");
      } finally {
        setCargando(false);
      }
    },
    [getToken, publicacionesOriginales],
  );

  return { publicaciones, cargando, mensajeError, buscar, cargarInicial };
};
