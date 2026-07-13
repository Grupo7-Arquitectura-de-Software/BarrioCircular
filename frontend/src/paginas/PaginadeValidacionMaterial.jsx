import { useEffect, useMemo, useRef, useState } from "react";
import {
  Badge,
  Box,
  Button,
  Field,
  Flex,
  HStack,
  Image,
  Input,
  InputGroup,
  Link,
  Spinner,
  Text,
  Textarea,
  VStack,
} from "@chakra-ui/react";
import { useAuth } from "@clerk/clerk-react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { MdArrowBack, MdCheckCircleOutline, MdOutlineImage } from "react-icons/md";

import { toaster } from "@/components/ui/toaster-instance";
import Icono from "@/componentes/atomos/Icono.jsx";
import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion.jsx";
import { confirmarRecoleccion } from "@/servicios/logisticaService";
import { obtenerPublicacion } from "@/servicios/publicacionService";
import { etiquetaTipoResiduo } from "@/utilidades/catalogoMateriales";
import { NAVEGACION_RECOLECTOR, SUBTITULO_RECOLECTOR } from "@/utilidades/navegacionPanel";

const OBSERVACIONES_RAPIDAS = [
  "El peso real fue menor.",
  "Material húmedo.",
  "Contiene elementos no reciclables.",
];

const MAX_OBSERVACIONES = 1000;

const normalizarNumero = (valor) => Number(String(valor).replace(",", "."));

const combinarObservaciones = (seleccionadas, observacionLibre) => {
  const partes = [...seleccionadas, observacionLibre].map((texto) => texto?.trim()).filter(Boolean);
  const unicas = [...new Set(partes)];
  const combinadas = unicas.join(" ");
  return combinadas.length > 0 ? combinadas : null;
};

const PaginadeValidacionMaterial = () => {
  const navigate = useNavigate();
  const { getToken } = useAuth();
  const { id: publicacionId } = useParams();
  const [searchParams] = useSearchParams();
  const rutaId = searchParams.get("rutaId");
  const paradaId = searchParams.get("paradaId");
  const pesoInicializadoRef = useRef(false);

  const [publicacion, setPublicacion] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [mensajeError, setMensajeError] = useState("");
  const [pesoRealVerificado, setPesoRealVerificado] = useState("");
  const [observacionesRapidas, setObservacionesRapidas] = useState([]);
  const [observacionLibre, setObservacionLibre] = useState("");
  const [confirmando, setConfirmando] = useState(false);

  useEffect(() => {
    let cancelado = false;

    const cargarPublicacion = async () => {
      if (!publicacionId || !rutaId || !paradaId) {
        setMensajeError("Faltan datos de la ruta para verificar esta recolección.");
        setCargando(false);
        return;
      }

      setCargando(true);
      setMensajeError("");
      try {
        const token = await getToken();
        if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
        const resultado = await obtenerPublicacion(token, publicacionId);
        if (cancelado) return;
        setPublicacion(resultado);
        if (!pesoInicializadoRef.current) {
          setPesoRealVerificado(String(resultado.pesoKg ?? ""));
          pesoInicializadoRef.current = true;
        }
      } catch (error) {
        if (!cancelado) {
          setMensajeError(error.message || "No fue posible cargar la publicación.");
        }
      } finally {
        if (!cancelado) setCargando(false);
      }
    };

    cargarPublicacion();

    return () => {
      cancelado = true;
    };
  }, [getToken, paradaId, publicacionId, rutaId]);

  const observacionesCombinadas = useMemo(
    () => combinarObservaciones(observacionesRapidas, observacionLibre),
    [observacionLibre, observacionesRapidas],
  );

  const alternarObservacionRapida = (observacion) => {
    setObservacionesRapidas((actuales) =>
      actuales.includes(observacion)
        ? actuales.filter((item) => item !== observacion)
        : [...actuales, observacion],
    );
  };

  const confirmarOperacion = async () => {
    if (confirmando) return;
    const peso = normalizarNumero(pesoRealVerificado);

    if (!Number.isFinite(peso) || peso <= 0) {
      toaster.create({
        title: "Peso inválido",
        description: "Ingresa un peso real mayor que 0 kg.",
        type: "error",
        duration: 4000,
      });
      return;
    }
    if (observacionesCombinadas && observacionesCombinadas.length > MAX_OBSERVACIONES) {
      toaster.create({
        title: "Observaciones muy largas",
        description: "Reduce las observaciones a máximo 1000 caracteres.",
        type: "error",
        duration: 4000,
      });
      return;
    }

    setConfirmando(true);
    try {
      const token = await getToken();
      if (!token) throw new Error("No se obtuvo un token de sesión de Clerk.");
      const resultado = await confirmarRecoleccion(token, rutaId, paradaId, {
        pesoRealVerificado: peso,
        observaciones: observacionesCombinadas,
      });

      toaster.create({
        title: resultado.rutaTerminada ? "Ruta completada" : "Operación confirmada",
        description: resultado.rutaTerminada
          ? "Ruta completada correctamente."
          : "La parada quedó completada. Continúa con la siguiente.",
        type: "success",
        duration: 3500,
      });

      navigate("/recolector/ruta-recoleccion", {
        state: { rutaCompletada: resultado.rutaTerminada },
      });
    } catch (error) {
      toaster.create({
        title: "No se pudo confirmar",
        description: error.message || "Intenta de nuevo más tarde.",
        type: "error",
        duration: 4500,
      });
    } finally {
      setConfirmando(false);
    }
  };

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_RECOLECTOR}
      subtituloMarca={SUBTITULO_RECOLECTOR}
      mostrarBuscador={false}
      anchoContenido="1080px"
    >
      <VStack align="stretch" gap={6}>
        <VStack align="stretch" gap={1}>
          <Link
            onClick={() => navigate(-1)}
            color="marca.primario"
            fontSize="sm"
            fontWeight="600"
            display="inline-flex"
            alignItems="center"
            gap={1}
            w="fit-content"
          >
            <MdArrowBack /> Volver
          </Link>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Verificar material recolectado
          </Text>
          <Text color="gray.600">Confirma el peso real antes de cerrar la operación.</Text>
        </VStack>

        {cargando ? (
          <Flex justify="center" py={16}>
            <Spinner size="lg" color="marca.primario" />
          </Flex>
        ) : mensajeError ? (
          <Box bg="fondo.tarjeta" border="1px solid" borderColor="red.200" borderRadius="xl" p={6}>
            <Text fontWeight="700" color="marca.error">
              No se pudo cargar la verificación
            </Text>
            <Text color="gray.600" mt={1}>
              {mensajeError}
            </Text>
          </Box>
        ) : (
          <Flex gap={6} align="flex-start" direction={{ base: "column", lg: "row" }}>
            <Box
              w={{ base: "100%", lg: "320px" }}
              flexShrink={0}
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              p={5}
            >
              <Flex justify="space-between" align="flex-start" gap={3} mb={4}>
                <Box>
                  <Text fontSize="sm" color="gray.600">
                    Publicación
                  </Text>
                  <Text fontFamily="heading" fontWeight="700" fontSize="xl">
                    {etiquetaTipoResiduo(publicacion.tipoResiduo)}
                  </Text>
                </Box>
                <Badge bg="fondo.cabeceraTarjeta" color="marca.secundario" borderRadius="md" px={2}>
                  {publicacion.estado}
                </Badge>
              </Flex>

              <Box
                h="180px"
                bg="fondo.pagina"
                border="1px solid"
                borderColor="gray.200"
                borderRadius="lg"
                overflow="hidden"
                mb={4}
              >
                {publicacion.evidenciaUrl ? (
                  <Image
                    src={publicacion.evidenciaUrl}
                    alt="Evidencia del material"
                    w="100%"
                    h="100%"
                    objectFit="cover"
                  />
                ) : (
                  <Flex h="100%" align="center" justify="center" color="gray.300">
                    <Icono componente={<MdOutlineImage />} tamanio="3xl" color="gray.300" />
                  </Flex>
                )}
              </Box>

              <VStack align="stretch" gap={2} fontSize="sm">
                <Flex justify="space-between" gap={3}>
                  <Text color="gray.600">Peso publicado</Text>
                  <Text fontWeight="700">{Number(publicacion.pesoKg).toFixed(1)} kg</Text>
                </Flex>
                <Flex justify="space-between" gap={3}>
                  <Text color="gray.600">Precio por kg</Text>
                  <Text fontWeight="700">${Number(publicacion.precioPorKilo).toFixed(2)}</Text>
                </Flex>
              </VStack>
            </Box>

            <Box
              flex="1"
              w="100%"
              bg="fondo.tarjeta"
              border="1px solid"
              borderColor="gray.200"
              borderRadius="xl"
              p={6}
            >
              <VStack align="stretch" gap={6}>
                <Field.Root required>
                  <Field.Label fontWeight="600">Peso real verificado</Field.Label>
                  <InputGroup endElement={<Text color="gray.500">kg</Text>}>
                    <Input
                      value={pesoRealVerificado}
                      onChange={(evento) => setPesoRealVerificado(evento.target.value)}
                      placeholder="Ej. 14.5"
                      type="number"
                      step="0.1"
                      min="0"
                      size="lg"
                      bg="fondo.pagina"
                      rounded="lg"
                    />
                  </InputGroup>
                  <Field.HelperText>
                    Peso publicado: {Number(publicacion.pesoKg).toFixed(1)} kg.
                  </Field.HelperText>
                </Field.Root>

                <Box>
                  <Text fontWeight="600" fontSize="sm" mb={3}>
                    Observaciones rápidas
                  </Text>
                  <HStack gap={2} wrap="wrap">
                    {OBSERVACIONES_RAPIDAS.map((observacion) => {
                      const seleccionada = observacionesRapidas.includes(observacion);
                      return (
                        <Button
                          key={observacion}
                          size="sm"
                          rounded="lg"
                          variant={seleccionada ? "solid" : "outline"}
                          colorPalette="verde"
                          onClick={() => alternarObservacionRapida(observacion)}
                        >
                          {observacion}
                        </Button>
                      );
                    })}
                  </HStack>
                </Box>

                <Field.Root>
                  <Field.Label fontWeight="600">Otra observación (opcional)</Field.Label>
                  <Textarea
                    value={observacionLibre}
                    onChange={(evento) => setObservacionLibre(evento.target.value)}
                    placeholder="Añade notas adicionales sobre la recolección..."
                    rows={4}
                    bg="fondo.pagina"
                    rounded="lg"
                    resize="none"
                    maxLength={MAX_OBSERVACIONES}
                  />
                  <Field.HelperText>
                    {observacionesCombinadas?.length || 0}/{MAX_OBSERVACIONES} caracteres.
                  </Field.HelperText>
                </Field.Root>

                <Flex justify="flex-end" borderTop="1px solid" borderColor="gray.100" pt={5}>
                  <Button
                    colorPalette="verde"
                    bg="marca.primario"
                    rounded="lg"
                    minW={{ base: "100%", sm: "220px" }}
                    loading={confirmando}
                    loadingText="Confirmando"
                    disabled={confirmando}
                    onClick={confirmarOperacion}
                  >
                    <MdCheckCircleOutline /> Confirmar operación
                  </Button>
                </Flex>
              </VStack>
            </Box>
          </Flex>
        )}
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginadeValidacionMaterial;
