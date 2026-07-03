import { useState } from "react";
import { useAuth, useUser } from "@clerk/clerk-react";
import { Box, Button, Circle, Field, Flex, Input, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import {
  MdArrowForward,
  MdOutlineFactory,
  MdOutlineHelpOutline,
  MdOutlineLocalShipping,
  MdOutlinePerson,
} from "react-icons/md";

import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import SelectorDeRol from "../componentes/moleculas/SelectorDeRol.jsx";
import Icono from "../componentes/atomos/Icono.jsx";
import { toaster } from "@/components/ui/toaster-instance";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { completarPerfil } from "@/servicios/perfilService";
import { obtenerRutaPrincipalPorRol } from "@/utilidades/rutasPerfil";
import { CLAVE_ROL_PRESELECCIONADO } from "./PaginadeSeleccionRol.jsx";

const ROLES = [
  { valor: "CIUDADANO", etiqueta: "Residente", icono: <MdOutlinePerson /> },
  { valor: "CENTRO_RECOLECCION", etiqueta: "Centro de Acopio", icono: <MdOutlineFactory /> },
  { valor: "RECICLADOR", etiqueta: "Reciclador", icono: <MdOutlineLocalShipping /> },
];

const COORDENADAS_QUITO = {
  latitud: -0.1807,
  longitud: -78.4678,
};

const obtenerRolPreseleccionado = () => {
  const rolGuardado = sessionStorage.getItem(CLAVE_ROL_PRESELECCIONADO);
  return ROLES.some((rol) => rol.valor === rolGuardado) ? rolGuardado : "";
};

const obtenerMensajeError = (error) => {
  if (esErrorApiConEstado(error, 403)) {
    return "La sesión no está autorizada para completar este perfil.";
  }
  if (esErrorApiConEstado(error, 404)) {
    return "No se encontró la cuenta asociada. Vuelve a iniciar sesión.";
  }
  if (esErrorApiConEstado(error, 409)) {
    return "Ya existe un perfil o documento registrado con estos datos.";
  }
  return "No se pudo completar el perfil. Intenta nuevamente.";
};

const PaginaCompletarPerfil = () => {
  const navigate = useNavigate();
  const { getToken } = useAuth();
  const { isLoaded, user } = useUser();
  const correoClerk = user?.primaryEmailAddress?.emailAddress;
  const [datosFormulario, setDatosFormulario] = useState({
    rol: obtenerRolPreseleccionado(),
    documentoIdentificacion: "",
    nombreCompleto: "",
    nombreComercial: "",
    correoElectronico: "",
    telefono: "",
    direccionTexto: "",
    ...COORDENADAS_QUITO,
  });
  const [estaEnviando, setEstaEnviando] = useState(false);
  const [estaObteniendoUbicacion, setEstaObteniendoUbicacion] = useState(false);

  const actualizarCampo = (evento) => {
    const { name, value } = evento.target;
    setDatosFormulario((datosActuales) => ({
      ...datosActuales,
      [name]: value,
    }));
  };

  const actualizarRol = (rol) => {
    setDatosFormulario((datosActuales) => ({ ...datosActuales, rol }));
  };

  const usarUbicacionActual = () => {
    if (!navigator.geolocation) {
      setDatosFormulario((datosActuales) => ({
        ...datosActuales,
        ...COORDENADAS_QUITO,
      }));
      toaster.create({
        title: "Ubicación no disponible",
        description:
          "No se pudo obtener tu ubicación. Puedes escribir una dirección o referencia manualmente.",
        type: "warning",
        duration: 4000,
      });
      return;
    }

    setEstaObteniendoUbicacion(true);
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        setDatosFormulario((datosActuales) => ({
          ...datosActuales,
          latitud: coords.latitude,
          longitud: coords.longitude,
        }));
        setEstaObteniendoUbicacion(false);
        toaster.create({
          title: "Ubicación detectada",
          description: "Ubicación actual detectada correctamente.",
          type: "success",
          duration: 3000,
        });
      },
      () => {
        setDatosFormulario((datosActuales) => ({
          ...datosActuales,
          ...COORDENADAS_QUITO,
        }));
        setEstaObteniendoUbicacion(false);
        toaster.create({
          title: "No se pudo obtener la ubicación",
          description:
            "No se pudo obtener tu ubicación. Puedes escribir una dirección o referencia manualmente.",
          type: "warning",
          duration: 4000,
        });
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 },
    );
  };

  const enviarFormulario = async (evento) => {
    evento.preventDefault();

    if (!datosFormulario.rol) {
      toaster.create({
        title: "Selecciona un rol",
        description: "Indica cómo te registras para continuar.",
        type: "warning",
        duration: 3000,
      });
      return;
    }

    setEstaEnviando(true);

    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");

      // TODO: enviar direccionTexto cuando el contrato de Perfiles lo soporte.
      const datosPerfil = {
        rol: datosFormulario.rol,
        documentoIdentificacion: datosFormulario.documentoIdentificacion,
        nombreCompleto: datosFormulario.nombreCompleto,
        nombreComercial: datosFormulario.nombreComercial,
        correoElectronico: correoClerk || datosFormulario.correoElectronico,
        telefono: datosFormulario.telefono,
        latitud: Number(datosFormulario.latitud),
        longitud: Number(datosFormulario.longitud),
      };
      const perfilCreado = await completarPerfil(token, datosPerfil);
      const rutaPrincipal = obtenerRutaPrincipalPorRol(perfilCreado.rol);

      if (!rutaPrincipal) {
        throw new Error(`El rol ${perfilCreado.rol} no tiene una ruta configurada.`);
      }

      sessionStorage.removeItem(CLAVE_ROL_PRESELECCIONADO);

      toaster.create({
        title: "Perfil completado",
        description: "Tu perfil ha sido configurado exitosamente.",
        type: "success",
        duration: 3000,
      });

      navigate(rutaPrincipal, { replace: true });
    } catch (error) {
      toaster.create({
        title: "No se pudo completar el perfil",
        description: obtenerMensajeError(error),
        type: "error",
        duration: 4000,
      });
    } finally {
      setEstaEnviando(false);
    }
  };

  return (
    <DiseniodeAutenticacion maxW="680px" relleno={0} conBarraSuperior>
      {/* Cabecera de la tarjeta */}
      <Flex bg="fondo.cabeceraTarjeta" px={6} py={4} justify="space-between" align="center">
        <Text fontFamily="heading" fontWeight="600" fontSize="lg">
          Información Básica
        </Text>
        <Circle size="28px" border="1px solid" borderColor="gray.400" color="gray.600">
          <Icono componente={<MdOutlineHelpOutline />} tamanio="sm" />
        </Circle>
      </Flex>

      <Box p={{ base: 5, md: 8 }}>
        <VStack gap={2} textAlign="center" mb={6}>
          <Text fontFamily="heading" fontWeight="700" fontSize="2xl">
            Bienvenido a BarrioCircular
          </Text>
          <Text fontSize="sm" color="gray.600" maxW="440px">
            Configuremos tu perfil para conectar con la economía circular local en Quito.
          </Text>
        </VStack>

        <form onSubmit={enviarFormulario}>
          <VStack gap={5} align="stretch">
            <Box>
              <Text fontSize="sm" fontWeight="600" mb={2}>
                Me registro como:
              </Text>
              <SelectorDeRol
                opciones={ROLES}
                valor={datosFormulario.rol}
                alCambiar={actualizarRol}
              />
            </Box>

            <Field.Root required>
              <Field.Label fontWeight="600">
                {datosFormulario.rol === "CENTRO_RECOLECCION"
                  ? "Nombre de la Organización"
                  : "Nombre Completo"}
              </Field.Label>
              <Input
                name="nombreCompleto"
                placeholder="ej., Javier Silva o EcoQuito Hub"
                value={datosFormulario.nombreCompleto}
                onChange={actualizarCampo}
                bg="fondo.pagina"
                required={datosFormulario.rol !== "CENTRO_RECOLECCION"}
              />
            </Field.Root>

            {datosFormulario.rol === "CENTRO_RECOLECCION" && (
              <Field.Root required>
                <Field.Label fontWeight="600">Nombre comercial</Field.Label>
                <Input
                  name="nombreComercial"
                  placeholder="ej., EcoQuito Hub"
                  value={datosFormulario.nombreComercial}
                  onChange={actualizarCampo}
                  bg="fondo.pagina"
                  required
                />
              </Field.Root>
            )}

            <Field.Root required>
              <Field.Label fontWeight="600">Documento de identidad / RUC</Field.Label>
              <Input
                name="documentoIdentificacion"
                placeholder="ej., 1712345678"
                value={datosFormulario.documentoIdentificacion}
                onChange={actualizarCampo}
                bg="fondo.pagina"
                required
              />
            </Field.Root>

            <Field.Root required>
              <Field.Label fontWeight="600">Correo electrónico</Field.Label>
              <Input
                name="correoElectronico"
                type="email"
                placeholder="ej., javier@ejemplo.com"
                value={correoClerk || datosFormulario.correoElectronico}
                onChange={actualizarCampo}
                disabled={Boolean(correoClerk)}
                bg="fondo.pagina"
                required
              />
              {isLoaded && !correoClerk && (
                <Field.HelperText color="orange.700">
                  Clerk no proporcionó un correo. Ingrésalo manualmente para continuar.
                </Field.HelperText>
              )}
            </Field.Root>

            <Field.Root required>
              <Field.Label fontWeight="600">Teléfono</Field.Label>
              <Input
                name="telefono"
                type="tel"
                placeholder="+593 99 123 4567"
                value={datosFormulario.telefono}
                onChange={actualizarCampo}
                bg="fondo.pagina"
                required
              />
            </Field.Root>

            {/* TODO: incorporar un selector con Google Maps/Places o Leaflet/OpenStreetMap. */}
            <Field.Root required>
              <Field.Label fontWeight="600">Ubicación en Quito</Field.Label>
              <Input
                name="direccionTexto"
                placeholder="Ej: Av. Universitaria y Bolivia, Quito"
                value={datosFormulario.direccionTexto}
                onChange={actualizarCampo}
                bg="fondo.pagina"
                required
              />
            </Field.Root>

            <Button
              type="button"
              variant="outline"
              colorPalette="verde"
              rounded="lg"
              onClick={usarUbicacionActual}
              loading={estaObteniendoUbicacion}
              loadingText="Obteniendo ubicación"
            >
              Usar mi ubicación actual
            </Button>

            <Flex justify="flex-end">
              <Button
                type="submit"
                colorPalette="verde"
                bg="marca.primario"
                rounded="lg"
                px={6}
                loading={estaEnviando}
                loadingText="Guardando perfil"
              >
                Guardar y Continuar <MdArrowForward />
              </Button>
            </Flex>
          </VStack>
        </form>
      </Box>
    </DiseniodeAutenticacion>
  );
};

export default PaginaCompletarPerfil;
