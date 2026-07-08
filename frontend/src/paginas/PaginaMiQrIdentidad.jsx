import { useEffect, useMemo, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { useLocation } from "react-router-dom";
import { QRCodeSVG } from "qrcode.react";
import {
  Box,
  Button,
  Flex,
  Link,
  Spinner,
  Text,
  VStack,
  useBreakpointValue,
} from "@chakra-ui/react";
import { MdContentCopy, MdOutlineBadge, MdQrCode2 } from "react-icons/md";

import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion.jsx";
import Icono from "@/componentes/atomos/Icono.jsx";
import { toaster } from "@/components/ui/toaster-instance";
import { emitirCredencialIdentidad } from "@/servicios/verificacionIdentidadService";
import {
  NAVEGACION_CENTRO,
  NAVEGACION_RECOLECTOR,
  SUBTITULO_CENTRO,
  SUBTITULO_RECOLECTOR,
} from "@/utilidades/navegacionPanel";

const DISENIO_POR_PREFIJO = {
  "/recolector": {
    navegacion: NAVEGACION_RECOLECTOR,
    subtituloMarca: SUBTITULO_RECOLECTOR,
    rutaNuevaPublicacion: "/recolector/vender/crear-publicacion",
  },
  "/centro": {
    navegacion: NAVEGACION_CENTRO,
    subtituloMarca: SUBTITULO_CENTRO,
  },
};

const formatearFecha = (fecha) => {
  if (!fecha) return "No disponible";
  return new Intl.DateTimeFormat("es-EC", {
    dateStyle: "long",
    timeStyle: "short",
  }).format(new Date(fecha));
};

const obtenerDisenio = (pathname) =>
  Object.entries(DISENIO_POR_PREFIJO).find(([prefijo]) => pathname.startsWith(prefijo))?.[1] ||
  DISENIO_POR_PREFIJO["/recolector"];

const PaginaMiQrIdentidad = () => {
  const { getToken } = useAuth();
  const { pathname } = useLocation();
  const [credencial, setCredencial] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const consultaIniciadaRef = useRef(false);
  const tamanioQr = useBreakpointValue({ base: 200, sm: 220, md: 260 }) || 220;

  const disenio = useMemo(() => obtenerDisenio(pathname), [pathname]);

  useEffect(() => {
    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const cargarCredencial = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        setCredencial(await emitirCredencialIdentidad(token));
      } catch (error) {
        setMensajeError(error.message || "No fue posible generar tu credencial de identidad.");
      } finally {
        setCargando(false);
      }
    };

    cargarCredencial();
  }, [getToken]);

  const copiarEnlace = async () => {
    if (!credencial?.urlVerificacion) return;

    try {
      await navigator.clipboard.writeText(credencial.urlVerificacion);
      toaster.create({
        title: "Enlace copiado",
        description: "La URL pública de verificación está lista para compartir.",
        type: "success",
        duration: 3000,
      });
    } catch {
      toaster.create({
        title: "No se pudo copiar",
        description: "Selecciona y copia el enlace manualmente.",
        type: "warning",
        duration: 3500,
      });
    }
  };

  return (
    <DiseniodeAplicacion
      navegacion={disenio.navegacion}
      subtituloMarca={disenio.subtituloMarca}
      rutaNuevaPublicacion={disenio.rutaNuevaPublicacion}
      mostrarBuscador={false}
      anchoContenido="860px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Mi QR de identidad
          </Text>
          <Text color="gray.600">
            Muestra este código cuando vayas a recoger o gestionar materiales. Cualquier persona
            podrá escanearlo para confirmar que tu identidad está activa en Barrio Circular.
          </Text>
        </VStack>

        <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
          {cargando ? (
            <VStack py={14} gap={4}>
              <Spinner size="lg" color="marca.primario" />
              <Text color="gray.600">Generando tu credencial...</Text>
            </VStack>
          ) : mensajeError ? (
            <VStack py={12} gap={3} textAlign="center">
              <Icono componente={<MdOutlineBadge />} tamanio="4xl" color="marca.error" />
              <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                No se pudo cargar tu credencial
              </Text>
              <Text color="gray.600" maxW="520px">
                {mensajeError}
              </Text>
            </VStack>
          ) : (
            <VStack gap={6} align="center">
              <Box
                bg="white"
                p={{ base: 4, md: 6 }}
                border="1px solid"
                borderColor="gray.200"
                borderRadius="xl"
                boxShadow="sm"
              >
                <QRCodeSVG
                  value={credencial.urlVerificacion}
                  size={tamanioQr}
                  level="H"
                  includeMargin
                />
              </Box>

              <VStack gap={1} textAlign="center">
                <Text fontWeight="700" color="marca.primario">
                  Credencial activa
                </Text>
                <Text fontSize="sm" color="gray.600">
                  Vigente hasta: {formatearFecha(credencial.fechaExpiracion)}
                </Text>
              </VStack>

              <Box
                w="100%"
                bg="fondo.cabeceraTarjeta"
                border="1px solid"
                borderColor="gray.200"
                borderRadius="lg"
                p={4}
              >
                <Text fontSize="sm" fontWeight="600" mb={2}>
                  Enlace público de verificación
                </Text>
                <Link
                  href={credencial.urlVerificacion}
                  target="_blank"
                  rel="noreferrer"
                  color="marca.primario"
                  fontSize="sm"
                  overflowWrap="anywhere"
                >
                  {credencial.urlVerificacion}
                </Link>
              </Box>

              <Flex justify="center">
                <Button
                  colorPalette="verde"
                  bg="marca.primario"
                  rounded="lg"
                  onClick={copiarEnlace}
                >
                  <MdContentCopy /> Copiar enlace
                </Button>
              </Flex>
            </VStack>
          )}
        </Box>

        <Box bg="fondo.cabeceraTarjeta" borderRadius="lg" p={4}>
          <Flex gap={3} align="flex-start">
            <Icono componente={<MdQrCode2 />} tamanio="xl" color="marca.secundario" />
            <Text fontSize="sm" color="gray.700">
              La página pública solo confirma si la identidad está vigente y activa. No comparte
              datos sensibles ni permite gestionar tu cuenta.
            </Text>
          </Flex>
        </Box>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaMiQrIdentidad;
