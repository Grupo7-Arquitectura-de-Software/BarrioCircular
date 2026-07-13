import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Flex, HStack, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { MdArrowBack } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import DetallePublicacion from "../componentes/organismos/DetallePublicacion";
import Icono from "../componentes/atomos/Icono.jsx";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";
import {
  barrioMasCercano,
  etiquetaEstadoPublicacion,
  etiquetaTipoResiduo,
} from "@/utilidades/barriosQuito";
import { obtenerPublicacion } from "@/servicios/publicacionService";
import { useReservarPublicacion } from "@/utilidades/useReservarPublicacion";

const PaginaDetallePublicacion = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { getToken } = useAuth();
  const esCentro = rol === "centro";
  const [publicacion, setPublicacion] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const consultaIniciadaRef = useRef(false);
  const { reservar, reservandoId } = useReservarPublicacion(rol);

  useEffect(() => {
    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const cargar = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        setPublicacion(await obtenerPublicacion(token, id));
      } catch (error) {
        setMensajeError(error.message || "No fue posible cargar la publicación.");
      } finally {
        setCargando(false);
      }
    };

    cargar();
  }, [getToken, id]);

  return (
    <DiseniodeAplicacion
      navegacion={esCentro ? NAVEGACION_CENTRO : NAVEGACION_RECOLECTOR}
      subtituloMarca={esCentro ? SUBTITULO_CENTRO : SUBTITULO_RECOLECTOR}
      mostrarBuscador={false}
      anchoContenido="1160px"
    >
      <VStack align="stretch" gap={6}>
        <HStack gap={3}>
          <Icono
            componente={
              <MdArrowBack cursor="pointer" onClick={() => navigate(-1)} aria-label="Volver" />
            }
            tamanio="xl"
          />
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Detalle de Material
          </Text>
        </HStack>

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : mensajeError ? (
          <Text color="marca.error">{mensajeError}</Text>
        ) : (
          <DetallePublicacion
            tipoMaterial={etiquetaTipoResiduo(publicacion.tipoResiduo)}
            pesoKg={publicacion.pesoKg}
            precioPorKilo={`$${Number(publicacion.precioPorKilo).toFixed(2)}`}
            descripcion={`Publicado el ${new Date(publicacion.fechaCreacion).toLocaleDateString()}.`}
            estado={etiquetaEstadoPublicacion(publicacion.estado)}
            vendedor={publicacion.nombreCreador || "Vendedor de la comunidad"}
            telefonoCreador={publicacion.telefonoCreador}
            rotuloVendedor="VENDEDOR"
            calificacionVendedor="—"
            detalleCalificacion="Sin calificaciones aún"
            ubicacion={`${barrioMasCercano(publicacion.latitud, publicacion.longitud)}, Quito`}
            imagenUrl={publicacion.evidenciaUrl}
            alReservar={() => reservar(id)}
            reservando={reservandoId === id}
          />
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaDetallePublicacion;
