import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";

import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { finalizarPublicacion } from "@/servicios/publicacionService";

const obtenerMensajeError = (error) => {
  if (esErrorApiConEstado(error, 409)) {
    return "Esta publicación ya no puede finalizarse en su estado actual.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Solo el creador o quien reservó la publicación puede finalizarla.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "La publicación ya no existe.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo finalizar la publicación.";
};

/**
 * Cierra el ciclo de una reserva (POST /publicaciones/{id}/finalizar),
 * RESERVADA → FINALIZADA. Al finalizar, invoca `alFinalizar` con el
 * resultado para que la vista pueda actualizar su lista local.
 */
export const useFinalizarPublicacion = () => {
  const { getToken } = useAuth();
  const [finalizandoId, setFinalizandoId] = useState(null);

  const finalizar = async (publicacionId, alFinalizar) => {
    setFinalizandoId(publicacionId);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      const resultado = await finalizarPublicacion(token, publicacionId);

      toaster.create({
        title: "Transacción finalizada",
        description: "La publicación se movió a tu historial.",
        type: "success",
        duration: 3000,
      });
      alFinalizar?.(resultado);
    } catch (error) {
      toaster.create({
        title: "No se pudo finalizar",
        description: obtenerMensajeError(error),
        type: "error",
        duration: 4500,
      });
    } finally {
      setFinalizandoId(null);
    }
  };

  return { finalizar, finalizandoId };
};
