import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { useNavigate } from "react-router-dom";
import { Spinner, Text, VStack } from "@chakra-ui/react";

import { registrarSesion } from "@/servicios/accesoService";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { obtenerMiPerfil } from "@/servicios/perfilService";
import { estaEnAreaDeRol, obtenerRutaPrincipalPorRol } from "@/utilidades/rutasPerfil";

const RUTA_AUTENTICACION = "/auth";
const RUTA_COMPLETAR_PERFIL = "/completar-perfil";

const ValidadorSesion = ({ children }) => {
  const { isLoaded, isSignedIn, getToken } = useAuth();
  const navigate = useNavigate();
  const [estaVerificando, setEstaVerificando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const verificacionIniciadaRef = useRef(false);
  const componenteActivoRef = useRef(true);

  useEffect(() => {
    componenteActivoRef.current = true;

    if (!isLoaded) {
      return () => {
        componenteActivoRef.current = false;
      };
    }

    if (!isSignedIn) {
      verificacionIniciadaRef.current = false;

      if (!window.location.pathname.startsWith(RUTA_AUTENTICACION)) {
        navigate(RUTA_AUTENTICACION, { replace: true });
      }

      return () => {
        componenteActivoRef.current = false;
      };
    }

    if (verificacionIniciadaRef.current) {
      return () => {
        componenteActivoRef.current = false;
      };
    }

    verificacionIniciadaRef.current = true;

    const verificarSesionYPerfil = async () => {
      setEstaVerificando(true);
      setMensajeError("");

      try {
        const token = await getToken();
        if (!token) throw new Error("Clerk no entregó un token de sesión válido.");

        await registrarSesion(token);

        let perfilUsuario;
        try {
          perfilUsuario = await obtenerMiPerfil(token);
        } catch (error) {
          if (esErrorApiConEstado(error, 404)) {
            if (window.location.pathname !== RUTA_COMPLETAR_PERFIL) {
              navigate(RUTA_COMPLETAR_PERFIL, { replace: true });
            }
            return;
          }
          throw error;
        }

        const rutaPrincipal = obtenerRutaPrincipalPorRol(perfilUsuario.rol);
        if (!rutaPrincipal) {
          throw new Error(`El rol ${perfilUsuario.rol} no tiene una ruta configurada.`);
        }

        const rutaActual = window.location.pathname;
        if (!estaEnAreaDeRol(rutaActual, perfilUsuario.rol)) {
          navigate(rutaPrincipal, { replace: true });
        }
      } catch (error) {
        if (componenteActivoRef.current) {
          setMensajeError(error.message || "No fue posible validar la sesión.");
        }
      } finally {
        if (componenteActivoRef.current) setEstaVerificando(false);
      }
    };

    verificarSesionYPerfil();

    return () => {
      componenteActivoRef.current = false;
    };
  }, [getToken, isLoaded, isSignedIn, navigate]);

  if (!isLoaded || (isSignedIn && estaVerificando)) {
    return (
      <VStack h="100vh" justify="center">
        <Spinner size="xl" />
        <Text>Validando sesión...</Text>
      </VStack>
    );
  }

  if (isSignedIn && mensajeError) {
    return (
      <VStack h="100vh" justify="center" px={6} textAlign="center">
        <Text fontWeight="bold">No se pudo validar tu sesión</Text>
        <Text color="gray.600">{mensajeError}</Text>
      </VStack>
    );
  }

  return children;
};

export default ValidadorSesion;
