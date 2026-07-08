import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { Box, Circle, Flex, Spinner, Text, VStack } from "@chakra-ui/react";
import { MdOutlineCancel, MdOutlineVerifiedUser } from "react-icons/md";

import BarraSuperiorPublica from "@/componentes/organismos/BarraSuperiorPublica.jsx";
import Icono from "@/componentes/atomos/Icono.jsx";
import { verificarCredencialPublica } from "@/servicios/verificacionIdentidadService";

const EstadoVerificacion = ({ valido, resultado }) => {
  if (valido) {
    return (
      <VStack gap={5} textAlign="center">
        <Circle size="72px" bg="verde.50" color="marca.primario">
          <Icono componente={<MdOutlineVerifiedUser />} tamanio="4xl" />
        </Circle>
        <VStack gap={2}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Identidad verificada
          </Text>
          <Text color="gray.600">
            Esta persona pertenece a Barrio Circular y su cuenta se encuentra activa.
          </Text>
        </VStack>
        <VStack
          align="stretch"
          gap={3}
          w="100%"
          bg="fondo.cabeceraTarjeta"
          borderRadius="lg"
          p={4}
          textAlign="left"
        >
          <Text>
            <Text as="span" fontWeight="700">
              Nombre:
            </Text>{" "}
            {resultado.nombreMostrado || "No disponible"}
          </Text>
          <Text>
            <Text as="span" fontWeight="700">
              Rol:
            </Text>{" "}
            {resultado.rol || "No disponible"}
          </Text>
          <Text>
            <Text as="span" fontWeight="700">
              Antigüedad en la plataforma:
            </Text>{" "}
            {resultado.antiguedadEnPlataformaDias ?? 0} días
          </Text>
        </VStack>
      </VStack>
    );
  }

  return (
    <VStack gap={5} textAlign="center">
      <Circle size="72px" bg="red.50" color="marca.error">
        <Icono componente={<MdOutlineCancel />} tamanio="4xl" />
      </Circle>
      <VStack gap={2}>
        <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
          Credencial no válida
        </Text>
        <Text color="gray.600">
          No se pudo confirmar que esta identidad esté activa en Barrio Circular. Por seguridad,
          no entregues materiales usando esta credencial.
        </Text>
      </VStack>
    </VStack>
  );
};

const PaginaVerificacionIdentidadPublica = () => {
  const { token } = useParams();
  const [resultado, setResultado] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [hayErrorRed, setHayErrorRed] = useState(false);
  const consultaIniciadaRef = useRef(false);

  useEffect(() => {
    if (consultaIniciadaRef.current) return;
    consultaIniciadaRef.current = true;

    const verificar = async () => {
      try {
        setResultado(await verificarCredencialPublica(token || ""));
      } catch {
        setHayErrorRed(true);
      } finally {
        setCargando(false);
      }
    };

    verificar();
  }, [token]);

  return (
    <Flex direction="column" minH="100vh" bg="fondo.pagina">
      <BarraSuperiorPublica />
      <Flex flex="1" align="center" justify="center" px={4} py={10}>
        <Box
          w="100%"
          maxW="560px"
          bg="fondo.tarjeta"
          border="1px solid"
          borderColor="gray.200"
          borderRadius="xl"
          boxShadow="md"
          p={{ base: 6, md: 8 }}
        >
          {cargando ? (
            <VStack py={12} gap={4}>
              <Spinner size="lg" color="marca.primario" />
              <Text color="gray.600">Verificando credencial...</Text>
            </VStack>
          ) : hayErrorRed ? (
            <VStack gap={4} textAlign="center">
              <Circle size="72px" bg="fondo.cabeceraTarjeta" color="marca.secundario">
                <Icono componente={<MdOutlineCancel />} tamanio="4xl" />
              </Circle>
              <Text fontFamily="heading" fontWeight="700" fontSize="2xl">
                No pudimos verificar la credencial
              </Text>
              <Text color="gray.600">
                Revisa tu conexión e intenta nuevamente en unos minutos.
              </Text>
            </VStack>
          ) : (
            <EstadoVerificacion valido={Boolean(resultado?.valido)} resultado={resultado || {}} />
          )}
        </Box>
      </Flex>
    </Flex>
  );
};

export default PaginaVerificacionIdentidadPublica;
