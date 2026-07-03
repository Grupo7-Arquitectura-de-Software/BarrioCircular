import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Box, Button, Flex, Spinner, Switch, Text, VStack } from "@chakra-ui/react";
import { MdOutlineLogout } from "react-icons/md";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";
import { obtenerMiPerfil } from "@/servicios/perfilService";
import { useCerrarSesion } from "@/utilidades/useCerrarSesion";

const ETIQUETAS_ROL = {
  CIUDADANO: "Ciudadano / Vendedor",
  RECICLADOR: "Reciclador",
  CENTRO_RECOLECCION: "Centro de Recolección",
};

const PREFERENCIAS_INICIALES = [
  { clave: "ofertas", etiqueta: "Nuevas ofertas en mis publicaciones", activa: true },
  { clave: "mensajes", etiqueta: "Mensajes de coordinación", activa: true },
  { clave: "resumen", etiqueta: "Resumen semanal de impacto", activa: false },
];

const FilaPerfil = ({ etiqueta, valor }) => (
  <Flex justify="space-between" py={3} borderBottom="1px solid" borderColor="gray.100" gap={4}>
    <Text color="gray.600" fontSize="sm">
      {etiqueta}
    </Text>
    <Text fontWeight="600" fontSize="sm" textAlign="right">
      {valor || "—"}
    </Text>
  </Flex>
);

const TarjetaSeccion = ({ titulo, children }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
    <Text fontFamily="heading" fontWeight="700" fontSize="lg" mb={4}>
      {titulo}
    </Text>
    {children}
  </Box>
);

/**
 * Configuración de la cuenta del ciudadano: datos del perfil (desde el
 * backend), preferencias de notificaciones y cierre de sesión.
 */
const PaginaConfiguracionCiudadano = () => {
  const { getToken } = useAuth();
  const cerrarSesion = useCerrarSesion();
  const [perfil, setPerfil] = useState(null);
  const [mensajeError, setMensajeError] = useState("");
  const [cargando, setCargando] = useState(true);
  const [preferencias, setPreferencias] = useState(PREFERENCIAS_INICIALES);
  const consultaIniciadaRef = useRef(false);

  useEffect(() => {
    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const cargarPerfil = async () => {
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        setPerfil(await obtenerMiPerfil(token));
      } catch (error) {
        setMensajeError(error.message || "No fue posible cargar tu perfil.");
      } finally {
        setCargando(false);
      }
    };

    cargarPerfil();
  }, [getToken]);

  const alternarPreferencia = (clave) => {
    setPreferencias((actuales) =>
      actuales.map((preferencia) =>
        preferencia.clave === clave ? { ...preferencia, activa: !preferencia.activa } : preferencia,
      ),
    );
  };

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CIUDADANO}
      rutaNuevaPublicacion={RUTA_NUEVA_PUBLICACION_CIUDADANO}
      mostrarBuscador={false}
      anchoContenido="720px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Configuración
          </Text>
          <Text color="gray.600">Administra tu perfil, notificaciones y sesión.</Text>
        </VStack>

        <TarjetaSeccion titulo="Mi Perfil">
          {cargando ? (
            <Flex justify="center" py={6}>
              <Spinner color="marca.primario" />
            </Flex>
          ) : mensajeError ? (
            <Text fontSize="sm" color="marca.error">
              {mensajeError}
            </Text>
          ) : (
            <VStack align="stretch" gap={0}>
              <FilaPerfil
                etiqueta="Nombre"
                valor={perfil?.nombreComercial || perfil?.nombreCompleto}
              />
              <FilaPerfil etiqueta="Correo electrónico" valor={perfil?.correoElectronico} />
              <FilaPerfil etiqueta="Teléfono" valor={perfil?.telefono} />
              <FilaPerfil etiqueta="Documento" valor={perfil?.documentoIdentificacion} />
              <FilaPerfil etiqueta="Rol" valor={ETIQUETAS_ROL[perfil?.rol] || perfil?.rol} />
              <Text fontSize="xs" color="gray.500" mt={3}>
                Para actualizar tus datos de perfil, contacta al soporte de la comunidad.
              </Text>
            </VStack>
          )}
        </TarjetaSeccion>

        <TarjetaSeccion titulo="Notificaciones">
          <VStack align="stretch" gap={4}>
            {preferencias.map((preferencia) => (
              <Flex key={preferencia.clave} justify="space-between" align="center" gap={4}>
                <Text fontSize="sm">{preferencia.etiqueta}</Text>
                <Switch.Root
                  checked={preferencia.activa}
                  onCheckedChange={() => alternarPreferencia(preferencia.clave)}
                  colorPalette="verde"
                >
                  <Switch.HiddenInput />
                  <Switch.Control>
                    <Switch.Thumb />
                  </Switch.Control>
                </Switch.Root>
              </Flex>
            ))}
          </VStack>
        </TarjetaSeccion>

        <TarjetaSeccion titulo="Sesión">
          <Flex justify="space-between" align="center" gap={4} wrap="wrap">
            <Text fontSize="sm" color="gray.600">
              Cierra tu sesión en este dispositivo.
            </Text>
            <Button variant="outline" colorPalette="red" rounded="lg" onClick={cerrarSesion}>
              <MdOutlineLogout /> Cerrar Sesión
            </Button>
          </Flex>
        </TarjetaSeccion>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaConfiguracionCiudadano;
