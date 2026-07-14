import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioCrearPublicacion from "../componentes/organismos/FormularioCrearPublicacion";
import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { ErrorSubidaEvidencia, subirEvidencia } from "@/servicios/almacenamientoService";
import { crearPublicacion } from "@/servicios/publicacionService";
import {
  NAVEGACION_CIUDADANO,
  NAVEGACION_RECOLECTOR,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

const obtenerMensajeError = (error) => {
  if (error instanceof ErrorSubidaEvidencia) {
    return "No se pudo subir la imagen de evidencia. Intenta nuevamente.";
  }
  if (esErrorApiConEstado(error, 403)) {
    return "Tu perfil no está autorizado para publicar materiales.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "No se encontró tu perfil. Completa tu perfil antes de publicar.";
  }
  if (esErrorApiConEstado(error, 0)) {
    return "No fue posible conectar con el backend.";
  }
  return error.message || "No se pudo crear la publicación. Intenta nuevamente.";
};

const PaginaCrearPublicaciones = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const { getToken } = useAuth();
  const esCiudadano = prefijoRuta === "/ciudadano";
  const [estaEnviando, setEstaEnviando] = useState(false);

  const publicarMaterial = async ({ archivoEvidencia, ...datosPublicacion }) => {
    setEstaEnviando(true);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      const evidenciaUrl = await subirEvidencia(archivoEvidencia);
      await crearPublicacion(token, { ...datosPublicacion, evidenciaUrl });

      toaster.create({
        title: "Publicación creada",
        description: "Tu material ya está disponible para la comunidad.",
        type: "success",
        duration: 3000,
      });
      navigate(`${prefijoRuta}/publicacion-disponible`);
    } catch (error) {
      toaster.create({
        title: "No se pudo publicar",
        description: obtenerMensajeError(error),
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
            Nueva Publicación
          </Text>
          <Text color="gray.600">
            Contribuye a la economía circular publicando materiales disponibles en tu área.
          </Text>
        </VStack>

        <FormularioCrearPublicacion
          alPublicar={publicarMaterial}
          alCancelar={() => navigate(-1)}
          estaEnviando={estaEnviando}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaCrearPublicaciones;
