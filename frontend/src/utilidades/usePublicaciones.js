import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";

/**
 * Carga publicaciones desde el backend con el token de Clerk.
 * `consultar` recibe (token) y devuelve la promesa del servicio a usar
 * (obtenerMisPublicaciones, obtenerPublicacionesDisponibles, etc.).
 */
export const usePublicaciones = (consultar) => {
  const { getToken } = useAuth();
  const [publicaciones, setPublicaciones] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const consultaIniciadaRef = useRef(false);

  useEffect(() => {
    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const cargar = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        setPublicaciones(await consultar(token));
      } catch (error) {
        setMensajeError(error.message || "No fue posible cargar las publicaciones.");
      } finally {
        setCargando(false);
      }
    };

    cargar();
  }, [getToken, consultar]);

  return { publicaciones, setPublicaciones, cargando, mensajeError };
};
