import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";

import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { eliminarPublicacion } from "@/servicios/publicacionService";

const obtenerMensajeError = (error) => {
  if (esErrorApiConEstado(error, 409)) {
    return "Solo se puede eliminar una publicación mientras está disponible.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Solo el creador puede eliminar la publicación.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "La publicación ya no existe.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo eliminar la publicación.";
};

/**
 * Elimina (soft-delete a CANCELADA) una publicación DISPONIBLE del creador
 * autenticado (DELETE /publicaciones/{id}).
 */
export const useEliminarPublicacion = () => {
  const { getToken } = useAuth();
  const [eliminandoId, setEliminandoId] = useState(null);

  const eliminar = async (publicacionId, alEliminar) => {
    setEliminandoId(publicacionId);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      await eliminarPublicacion(token, publicacionId);

      toaster.create({
        title: "Publicación eliminada",
        description: "Ya no aparecerá en tus publicaciones activas.",
        type: "success",
        duration: 3000,
      });
      alEliminar?.();
    } catch (error) {
      toaster.create({
        title: "No se pudo eliminar",
        description: obtenerMensajeError(error),
        type: "error",
        duration: 4500,
      });
    } finally {
      setEliminandoId(null);
    }
  };

  return { eliminar, eliminandoId };
};
