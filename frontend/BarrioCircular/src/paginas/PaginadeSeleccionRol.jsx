import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Box, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

import LogotipoApp from "../componentes/atomos/LogotipoApp";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { obtenerMiPerfil } from "@/servicios/perfilService";
import { obtenerRutaPrincipalPorRol } from "@/utilidades/rutasPerfil";

const PaginadeSeleccionRol = () => {
  const navigate = useNavigate();
  const { getToken, isLoaded, isSignedIn } = useAuth();
  const [mensajeError, setMensajeError] = useState("");
  const consultaIniciadaRef = useRef(false);

  useEffect(() => {
    if (!isLoaded) return;

    if (!isSignedIn) {
      navigate("/auth", { replace: true });
      return;
    }

    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const redirigirSegunPerfil = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

        const perfilUsuario = await obtenerMiPerfil(token);
        const rutaPrincipal = obtenerRutaPrincipalPorRol(perfilUsuario.rol);
        if (!rutaPrincipal) {
          throw new Error(`El rol ${perfilUsuario.rol} no tiene una ruta configurada.`);
        }
        navigate(rutaPrincipal, { replace: true });
      } catch (error) {
        if (esErrorApiConEstado(error, 404)) {
          navigate("/completar-perfil", { replace: true });
          return;
        }
        setMensajeError(error.message || "No fue posible consultar el perfil.");
      }
    };

    redirigirSegunPerfil();
  }, [getToken, isLoaded, isSignedIn, navigate]);

  return (
    <DiseniodeAutenticacion>
      <VStack gap={6} align="center" justify="center" minH="350px">
        <Box transform="scale(1.2)">
          <LogotipoApp tamanio="lg" />
        </Box>
        {mensajeError ? (
          <>
            <Text fontSize="xl" fontWeight="bold" textAlign="center">
              No pudimos cargar tu perfil
            </Text>
            <Text color="gray.600" textAlign="center">
              {mensajeError}
            </Text>
          </>
        ) : (
          <>
            <Spinner size="lg" />
            <Text color="gray.600" textAlign="center">
              Preparando tu experiencia según tu perfil...
            </Text>
          </>
        )}
      </VStack>
    </DiseniodeAutenticacion>
  );
};

export default PaginadeSeleccionRol;
