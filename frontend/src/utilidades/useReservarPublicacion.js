import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";

import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { reservarPublicacion } from "@/servicios/publicacionService";

const obtenerMensajeError = (error) => {
  if (esErrorApiConEstado(error, 409)) {
    return "Esta publicación ya fue reservada por otro comprador.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Tu perfil no está autorizado para reservar materiales.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "La publicación ya no está disponible.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo reservar la publicación.";
};

/**
 * Reserva directa de una publicación (POST /publicaciones/{id}/reservar),
 * según el flujo del documento 04: DISPONIBLE → RESERVADA. Al reservar,
 * navega a la coordinación de recolección del rol comprador.
 */
export const useReservarPublicacion = (rol) => {
  const { getToken } = useAuth();
  const navigate = useNavigate();
  const [reservandoId, setReservandoId] = useState(null);

  const reservar = async (publicacionId) => {
    setReservandoId(publicacionId);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      await reservarPublicacion(token, publicacionId);

      toaster.create({
        title: "Material reservado",
        description: "Ahora coordina la recolección con el vendedor.",
        type: "success",
        duration: 3000,
      });
      navigate(`/${rol}/coordinar/${publicacionId}`);
    } catch (error) {
      toaster.create({
        title: "No se pudo reservar",
        description: obtenerMensajeError(error),
        type: "error",
        duration: 4500,
      });
    } finally {
      setReservandoId(null);
    }
  };

  return { reservar, reservandoId };
};
