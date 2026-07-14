import { useEffect, useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import { Flex, Link, SimpleGrid, Spinner, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineFactory, MdOutlineHolidayVillage, MdOutlineLocalShipping } from "react-icons/md";

import BarraSuperiorPublica from "../componentes/organismos/BarraSuperiorPublica.jsx";
import TarjetaDeRol from "../componentes/moleculas/TarjetaDeRol.jsx";
import { esErrorApiConEstado } from "@/servicios/clienteApi";
import { obtenerMiPerfil } from "@/servicios/perfilService";
import { obtenerRutaPrincipalPorRol } from "@/utilidades/rutasPerfil";

export const CLAVE_ROL_PRESELECCIONADO = "rolPreseleccionado";

const ROLES_DISPONIBLES = [
  {
    valor: "CIUDADANO",
    titulo: "Ciudadano",
    descripcion: "Quiero reciclar desde casa y hacer seguimiento de mi impacto en la comunidad.",
    icono: <MdOutlineHolidayVillage />,
  },
  {
    valor: "CENTRO_RECOLECCION",
    titulo: "Centro",
    descripcion: "Quiero comprar materiales a gran escala y gestionar el procesamiento.",
    icono: <MdOutlineFactory />,
  },
  {
    valor: "RECICLADOR",
    titulo: "Reciclador",
    descripcion: "Recolecto, transporto y muevo materiales de manera eficiente.",
    icono: <MdOutlineLocalShipping />,
  },
];

const PaginadeSeleccionRol = () => {
  const navigate = useNavigate();
  const { getToken, isLoaded, isSignedIn } = useAuth();
  const [mensajeError, setMensajeError] = useState("");
  const consultaIniciadaRef = useRef(false);

  useEffect(() => {
    if (!isLoaded || !isSignedIn) return;

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

  const seleccionarRol = (rol) => {
    sessionStorage.setItem(CLAVE_ROL_PRESELECCIONADO, rol);
    navigate("/auth");
  };

  // Con sesión activa solo se muestra el estado de redirección.
  if (!isLoaded || isSignedIn) {
    return (
      <Flex direction="column" minH="100vh" bg="fondo.pagina" align="center" justify="center">
        <VStack gap={4} px={6} textAlign="center">
          {mensajeError ? (
            <>
              <Text fontFamily="heading" fontSize="xl" fontWeight="700">
                No pudimos cargar tu perfil
              </Text>
              <Text color="gray.600">{mensajeError}</Text>
            </>
          ) : (
            <>
              <Spinner size="lg" color="marca.primario" />
              <Text color="gray.600">Preparando tu experiencia según tu perfil...</Text>
            </>
          )}
        </VStack>
      </Flex>
    );
  }

  return (
    <Flex direction="column" minH="100vh" bg="fondo.pagina">
      <BarraSuperiorPublica />
      <VStack flex="1" justify="center" px={{ base: 4, md: 8 }} py={12} gap={10}>
        <VStack gap={3} maxW="640px" textAlign="center">
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "3xl", md: "4xl" }}>
            ¿Quién eres?
          </Text>
          <Text color="gray.600">
            Selecciona tu rol para ayudarnos a personalizar tu experiencia en BarrioCircular.
            Siempre puedes ajustar esto más tarde en la configuración.
          </Text>
        </VStack>

        <SimpleGrid
          columns={{ base: 1, md: 3 }}
          gap={6}
          w="100%"
          maxW="920px"
          justifyItems="center"
        >
          {ROLES_DISPONIBLES.map((rol) => (
            <TarjetaDeRol
              key={rol.valor}
              titulo={rol.titulo}
              descripcion={rol.descripcion}
              icono={rol.icono}
              alSeleccionar={() => seleccionarRol(rol.valor)}
            />
          ))}
        </SimpleGrid>

        <Text fontSize="sm" color="gray.600">
          ¿Ya tienes una cuenta?{" "}
          <Link color="marca.primario" fontWeight="600" onClick={() => navigate("/auth")}>
            Inicia sesión aquí
          </Link>
          .
        </Text>
      </VStack>
    </Flex>
  );
};

export default PaginadeSeleccionRol;
