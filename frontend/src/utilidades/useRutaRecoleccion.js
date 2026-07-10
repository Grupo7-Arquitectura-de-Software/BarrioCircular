import { useCallback, useState } from "react";
import { useAuth } from "@clerk/clerk-react";

import { esErrorApiConEstado } from "@/servicios/clienteApi";
import {
  construirRuta as construirRutaService,
  obtenerRutaActiva,
  actualizarRutaActiva,
  registrarLlegadaParada,
  iniciarRutaActiva,
} from "@/servicios/logisticaService";

const obtenerMensajeError = (error, mensajePorDefecto) => {
  if (esErrorApiConEstado(error, 404)) {
    return "No existe una ruta de recolección activa.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Tu perfil no está autorizado para gestionar rutas de recolección.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || mensajePorDefecto;
};

export const useRutaRecoleccion = () => {
  const { getToken } = useAuth();
  const [ruta, setRuta] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [mensajeError, setMensajeError] = useState("");
  const [construyendo, setConstruyendo] = useState(false);
  const [registroLlegada, setRegistroLlegada] = useState(false);
  const [actualizandoRuta, setActualizandoRuta] = useState(false);

  const obtenerTokenSesion = useCallback(async () => {
    const token = await getToken();
    if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
    return token;
  }, [getToken]);

  const cargarRutaActiva = useCallback(async () => {
    setCargando(true);
    setMensajeError("");
    try {
      const token = await obtenerTokenSesion();
      const rutaActiva = await obtenerRutaActiva(token);
      setRuta(rutaActiva);
      return rutaActiva;
    } catch (error) {
      setRuta(null);
      setMensajeError(
        obtenerMensajeError(error, "No fue posible cargar la ruta de recolección activa."),
      );
      return null;
    } finally {
      setCargando(false);
    }
  }, [obtenerTokenSesion]);

  const construirRuta = useCallback(
    async (datos) => {
      setConstruyendo(true);
      setMensajeError("");
      try {
        const token = await obtenerTokenSesion();
        const rutaConstruida = await construirRutaService(token, datos);
        setRuta(rutaConstruida);
        return rutaConstruida;
      } catch (error) {
        setMensajeError(obtenerMensajeError(error, "No fue posible construir la ruta."));
        return null;
      } finally {
        setConstruyendo(false);
      }
    },
    [obtenerTokenSesion],
  );

  const registrarLlegada = useCallback(
    async (rutaId, paradaId, datos) => {
      setRegistroLlegada(true);
      setMensajeError("");
      try {
        const token = await obtenerTokenSesion();
        // Si la ruta está planificada, iniciarla antes de registrar la llegada
        if (ruta && ruta.estado === "PLANIFICADA") {
          await iniciarRutaActiva(token);
          // refrescar la ruta activa localmente
          const rutaActiva = await obtenerRutaActiva(token);
          setRuta(rutaActiva);
        }
        const rutaActualizada = await registrarLlegadaParada(token, rutaId, paradaId, datos);
        setRuta(rutaActualizada);
        return rutaActualizada;
      } catch (error) {
        setMensajeError(
          obtenerMensajeError(error, "No fue posible registrar la llegada a la parada."),
        );
        return null;
      } finally {
        setRegistroLlegada(false);
      }
    },
    [obtenerTokenSesion, ruta],
  );

  const actualizarRuta = useCallback(
    async () => {
      setActualizandoRuta(true);
      setMensajeError("");
      try {
        const token = await obtenerTokenSesion();
        const rutaActualizada = await actualizarRutaActiva(token);
        setRuta(rutaActualizada);
        return rutaActualizada;
      } catch (error) {
        setMensajeError(obtenerMensajeError(error, "No fue posible actualizar la ruta."));
        return null;
      } finally {
        setActualizandoRuta(false);
      }
    },
    [obtenerTokenSesion],
  );

  return {
    ruta,
    cargando,
    mensajeError,
    construyendo,
    registrandoLlegada: registroLlegada,
    actualizandoRuta,
    cargarRutaActiva,
    construirRuta,
    actualizarRuta,
    registrarLlegada,
  };
};
