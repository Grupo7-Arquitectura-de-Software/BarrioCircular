import { useClerk } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";

const RUTA_AUTENTICACION = "/auth";

export const useCerrarSesion = () => {
  const { signOut } = useClerk();
  const navigate = useNavigate();

  return async () => {
    try {
      await signOut();
    } finally {
      navigate(RUTA_AUTENTICACION, { replace: true });
    }
  };
};
