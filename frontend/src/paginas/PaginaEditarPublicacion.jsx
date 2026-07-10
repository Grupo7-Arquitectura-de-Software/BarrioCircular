import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Flex, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioCrearPublicacion from "../componentes/organismos/FormularioCrearPublicacion";
import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { ErrorSubidaEvidencia, subirEvidencia } from "@/servicios/almacenamientoService";
import { actualizarPublicacion, obtenerPublicacion } from "@/servicios/publicacionService";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

const obtenerMensajeErrorCarga = (error) => {
  if (esErrorApiConEstado(error, 404)) {
    return "La publicación no existe o ya fue eliminada.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo cargar la publicación.";
};

const obtenerMensajeErrorGuardado = (error) => {
  if (error instanceof ErrorSubidaEvidencia) {
    return "No se pudo subir la nueva imagen de evidencia. Intenta nuevamente.";
  }
  if (esErrorApiConEstado(error, 409)) {
    return "Solo se puede editar una publicación mientras está disponible.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Solo el creador puede editar esta publicación.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "La publicación ya no existe.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo guardar la publicación. Intenta nuevamente.";
};

/**
 * Edición de una publicación existente: precarga el formulario de creación
 * con los datos actuales y envía los cambios vía PUT /api/publicaciones/{id}.
 */
const PaginaEditarPublicacion = ({ prefijoRuta = "/ciudadano" }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getToken } = useAuth();
  const esCiudadano = prefijoRuta === "/ciudadano";
  const [publicacion, setPublicacion] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const [estaEnviando, setEstaEnviando] = useState(false);
  const cargaIniciadaRef = useRef(false);

  useEffect(() => {
    if (cargaIniciadaRef.current) return;
    cargaIniciadaRef.current = true;

    const cargar = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        setPublicacion(await obtenerPublicacion(token, id));
      } catch (error) {
        setMensajeError(obtenerMensajeErrorCarga(error));
      } finally {
        setCargando(false);
      }
    };

    cargar();
  }, [getToken, id]);

  const guardarCambios = async ({ archivoEvidencia, ...datosPublicacion }) => {
    setEstaEnviando(true);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      const evidenciaUrl = archivoEvidencia
        ? await subirEvidencia(archivoEvidencia)
        : publicacion.evidenciaUrl;

      await actualizarPublicacion(token, id, { ...datosPublicacion, evidenciaUrl });

      toaster.create({
        title: "Publicación actualizada",
        description: "Los cambios se guardaron correctamente.",
        type: "success",
        duration: 3000,
      });
      navigate(`${prefijoRuta}/publicacion-disponible`);
    } catch (error) {
      toaster.create({
        title: "No se pudo guardar",
        description: obtenerMensajeErrorGuardado(error),
        type: "error",
        duration: 4500,
      });
    } finally {
      setEstaEnviando(false);
    }
  };

  return (
    <DiseniodeAplicacion
      navegacion={esCiudadano ? NAVEGACION_CIUDADANO : NAVEGACION_RECOLECTOR}
      rutaNuevaPublicacion={esCiudadano ? RUTA_NUEVA_PUBLICACION_CIUDADANO : undefined}
      mostrarBuscador={false}
      anchoContenido="760px"
      mostrarAtras={!esCiudadano}
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Editar Publicación
          </Text>
          <Text color="gray.600">Corrige el peso, precio, ubicación o material de tu anuncio.</Text>
        </VStack>

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : mensajeError ? (
          <Text color="marca.error">{mensajeError}</Text>
        ) : (
          <FormularioCrearPublicacion
            modoEdicion
            datosIniciales={publicacion}
            alPublicar={guardarCambios}
            alCancelar={() => navigate(-1)}
            estaEnviando={estaEnviando}
          />
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaEditarPublicacion;
