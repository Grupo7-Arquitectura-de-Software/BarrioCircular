import { useRef, useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import {
  Box,
  Button,
  Field,
  Flex,
  Input,
  InputGroup,
  SimpleGrid,
  Spinner,
  Text,
  VStack,
  createListCollection,
} from "@chakra-ui/react";
import { MdOutlineFileUpload, MdOutlineLocationOn } from "react-icons/md";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";
import AreaCargaImagenes from "../moleculas/AreaCargaImagenes.jsx";
import SelectorUbicacionMapa from "./SelectorUbicacionMapa.jsx";
import Icono from "../atomos/Icono.jsx";
import { toaster } from "@/components/ui/toaster-instance";
import {
  BARRIOS_QUITO,
  ETIQUETAS_TIPO_RESIDUO,
  etiquetaTipoResiduo,
  obtenerCoordenadasDeBarrio,
  valorBarrioMasCercano,
} from "@/utilidades/barriosQuito";
import { analizarMaterial } from "@/servicios/analisisMaterialService";

const tiposMaterial = createListCollection({
  items: Object.entries(ETIQUETAS_TIPO_RESIDUO).map(([value, label]) => ({ label, value })),
});

const barriosQuito = createListCollection({
  items: BARRIOS_QUITO.map((barrio) => ({ label: barrio.etiqueta, value: barrio.valor })),
});

const convertirArchivoABase64 = (archivo) =>
  new Promise((resolve, reject) => {
    const lector = new FileReader();
    lector.onload = () => resolve(lector.result);
    lector.onerror = reject;
    lector.readAsDataURL(archivo);
  });

const ETIQUETAS_ESTADO_MATERIAL = {
  EXCELENTE: "Excelente",
  BUENO: "Bueno",
  REGULAR: "Regular",
};

// Mensajes de UI para cada rechazo del análisis; la recomendación específica
// de la IA (qué se ve en la foto, cómo repetirla) se añade a continuación.
const MENSAJES_RECHAZO = {
  NO_ES_RECICLAJE:
    "La foto no muestra material de reciclaje. Sube una foto del material que quieres publicar.",
  FOTO_NO_CLARA: "La foto no se ve bien.",
  MULTIPLES_MATERIALES:
    "Detectamos varios materiales distintos. Toma una foto con un solo tipo de material.",
  MATERIAL_NO_SOPORTADO: "Por ahora solo aceptamos PET, cartón, vidrio y chatarra.",
};

const TarjetaSeccion = ({ titulo, children }) => (
  <Box bg="fondo.tarjeta" border="1px solid" borderColor="gray.200" borderRadius="xl" p={6}>
    <Text
      fontFamily="heading"
      fontWeight="600"
      fontSize="lg"
      pb={3}
      mb={5}
      borderBottom="1px solid"
      borderColor="gray.100"
    >
      {titulo}
    </Text>
    {children}
  </Box>
);

/**
 * Formulario "Nueva Publicación" con flujo imagen-primero: al subir la foto se
 * analiza automáticamente con IA. Si la foto es válida se autocompletan tipo,
 * peso y precio sugerido (todos editables); si es rechazada (no es reciclaje,
 * foto poco clara o varios materiales) se pide otra foto y se bloquea el envío.
 * Si la IA no está disponible, el formulario se completa manualmente.
 * Los datos se entregan vía `alPublicar` (POST /api/publicaciones).
 * En `modoEdicion`, se precarga con `datosIniciales` (PUT /api/publicaciones/{id})
 * y la foto de evidencia es opcional: si no se sube una nueva, se conserva la actual.
 */
const FormularioCrearPublicacion = ({
  alPublicar,
  alCancelar,
  estaEnviando = false,
  modoEdicion = false,
  datosIniciales,
}) => {
  const { getToken } = useAuth();
  const [tipoResiduo, setTipoResiduo] = useState(datosIniciales?.tipoResiduo || "");
  const [pesoKg, setPesoKg] = useState(datosIniciales ? String(datosIniciales.pesoKg) : "");
  const [precioPorKilo, setPrecioPorKilo] = useState(
    datosIniciales ? String(datosIniciales.precioPorKilo) : "",
  );
  const [ubicacion, setUbicacion] = useState(
    datosIniciales ? { latitud: datosIniciales.latitud, longitud: datosIniciales.longitud } : null,
  );
  const [archivoEvidencia, setArchivoEvidencia] = useState(null);
  // "inactivo" | "analizando" | "valido" | "rechazado" | "ia_no_disponible"
  const [estadoAnalisis, setEstadoAnalisis] = useState("inactivo");
  const [analisis, setAnalisis] = useState(null);
  // Identifica la petición de análisis vigente para descartar respuestas
  // obsoletas si el usuario cambia o quita la foto mientras se analiza.
  const peticionAnalisisVigente = useRef(0);

  const advertir = (titulo, descripcion) => {
    toaster.create({ title: titulo, description: descripcion, type: "warning", duration: 3500 });
  };

  const ejecutarAnalisis = async (archivo) => {
    const idPeticion = ++peticionAnalisisVigente.current;
    setEstadoAnalisis("analizando");
    setAnalisis(null);
    try {
      const [token, imagenBase64] = await Promise.all([
        getToken(),
        convertirArchivoABase64(archivo),
      ]);
      const resultado = await analizarMaterial(token, { imagenBase64 });
      if (idPeticion !== peticionAnalisisVigente.current) return;

      setAnalisis(resultado);
      if (resultado.resultado === "VALIDO") {
        setEstadoAnalisis("valido");
        setTipoResiduo(resultado.tipoMaterial);
        if (resultado.pesoEstimadoKg != null) setPesoKg(String(resultado.pesoEstimadoKg));
        setPrecioPorKilo(String(resultado.precioSugeridoPorKilo));
      } else if (resultado.resultado === "IA_NO_DISPONIBLE") {
        setEstadoAnalisis("ia_no_disponible");
        advertir(
          "No pudimos analizar la foto con IA",
          "Completa el tipo, peso y precio manualmente.",
        );
      } else {
        setEstadoAnalisis("rechazado");
      }
    } catch {
      if (idPeticion !== peticionAnalisisVigente.current) return;
      setEstadoAnalisis("ia_no_disponible");
      setAnalisis(null);
      advertir(
        "No pudimos analizar la foto con IA",
        "Completa el tipo, peso y precio manualmente.",
      );
    }
  };

  const alCambiarFoto = ({ acceptedFiles }) => {
    const archivo = acceptedFiles[0] ?? null;
    setArchivoEvidencia(archivo);
    if (!archivo) {
      peticionAnalisisVigente.current += 1;
      setEstadoAnalisis("inactivo");
      setAnalisis(null);
      return;
    }
    ejecutarAnalisis(archivo);
  };

  const mensajeRechazo = () => {
    const base = MENSAJES_RECHAZO[analisis?.resultado] || "La foto no pasó el análisis.";
    return analisis?.recomendacion ? `${base} ${analisis.recomendacion}` : base;
  };

  const enviarFormulario = (evento) => {
    evento.preventDefault();

    if (estadoAnalisis === "analizando") {
      advertir("Analizando la foto", "Espera a que termine el análisis con IA.");
      return;
    }
    if (estadoAnalisis === "rechazado") {
      advertir("La foto fue rechazada", mensajeRechazo());
      return;
    }
    if (!modoEdicion && !archivoEvidencia) {
      advertir("Falta la foto del material", "Sube una foto del material para publicar.");
      return;
    }
    if (!tipoResiduo) {
      advertir("Selecciona el tipo de material", "Elige una categoría del catálogo.");
      return;
    }
    if (!(Number(pesoKg) > 0)) {
      advertir("Peso inválido", "El peso estimado debe ser mayor que 0 kg.");
      return;
    }
    if (!(Number(precioPorKilo) > 0)) {
      advertir("Precio inválido", "El precio por kilo debe ser mayor que 0.");
      return;
    }
    if (!ubicacion) {
      advertir(
        "Selecciona la ubicación",
        "Marca el punto de recogida en el mapa o usa tu ubicación actual.",
      );
      return;
    }

    alPublicar?.({
      tipoResiduo,
      pesoKg: Number(pesoKg),
      precioPorKilo: Number(precioPorKilo),
      latitud: ubicacion.latitud,
      longitud: ubicacion.longitud,
      archivoEvidencia,
    });
  };

  return (
    <VStack as="form" onSubmit={enviarFormulario} gap={6} align="stretch" w="100%">
      <TarjetaSeccion titulo="Foto del Material">
        <Field.Root required={!modoEdicion}>
          <Text fontSize="sm" color="gray.500" mb={2}>
            {modoEdicion
              ? "Ya tienes una foto subida. Sube una nueva solo si quieres reemplazarla; se volverá a analizar con IA."
              : "Empieza subiendo la foto: la IA la validará y sugerirá el tipo, peso y precio del material."}
          </Text>
          <AreaCargaImagenes
            maximoArchivos={1}
            tamanioMaximoMB={10}
            alCambiarArchivos={alCambiarFoto}
          />

          {estadoAnalisis === "analizando" && (
            <Flex align="center" gap={2} mt={3}>
              <Spinner size="sm" color="marca.primario" />
              <Text fontSize="sm" color="gray.600">
                Analizando foto con IA…
              </Text>
            </Flex>
          )}
          {estadoAnalisis === "valido" && analisis && (
            <Box
              mt={3}
              p={3}
              bg="green.50"
              borderRadius="lg"
              border="1px solid"
              borderColor="green.200"
            >
              <Text fontSize="sm" color="green.700" fontWeight="600">
                Detectado: {etiquetaTipoResiduo(analisis.tipoMaterial)} · Estado:{" "}
                {ETIQUETAS_ESTADO_MATERIAL[analisis.estadoMaterial] || analisis.estadoMaterial}
              </Text>
              <Text fontSize="sm" color="green.700">
                Revisa las sugerencias de tipo, peso y precio: puedes modificarlas antes de
                publicar.
              </Text>
              {analisis.recomendacion && (
                <Text fontSize="sm" color="gray.600" mt={1}>
                  {analisis.recomendacion}
                </Text>
              )}
            </Box>
          )}
          {estadoAnalisis === "rechazado" && (
            <Box
              mt={3}
              p={3}
              bg="red.50"
              borderRadius="lg"
              border="1px solid"
              borderColor="red.200"
            >
              <Text fontSize="sm" color="red.700" fontWeight="600">
                {mensajeRechazo()}
              </Text>
              <Text fontSize="sm" color="red.600">
                Quita esta foto y sube otra para poder publicar.
              </Text>
            </Box>
          )}
          {estadoAnalisis === "ia_no_disponible" && (
            <Text fontSize="sm" color="orange.600" mt={3}>
              No pudimos analizar la foto con IA. Completa el tipo, peso y precio manualmente.
            </Text>
          )}
        </Field.Root>
      </TarjetaSeccion>

      <TarjetaSeccion titulo="Detalles del Material">
        <VStack gap={5} align="stretch">
          <SimpleGrid columns={{ base: 1, md: 2 }} gap={5}>
            <Field.Root required>
              <Field.Label fontWeight="600">Tipo de Material</Field.Label>
              <SelectorDesplegable
                titulo="Seleccionar categoría"
                colecciondeDatos={tiposMaterial}
                mostrarEtiqueta={false}
                valor={tipoResiduo}
                alCambiar={setTipoResiduo}
              />
            </Field.Root>
            <Field.Root required>
              <Field.Label fontWeight="600">Peso Estimado (kg)</Field.Label>
              <Input
                placeholder="ej., 5"
                type="number"
                min="0.1"
                step="0.1"
                bg="fondo.pagina"
                rounded="lg"
                value={pesoKg}
                onChange={(evento) => setPesoKg(evento.target.value)}
                required
              />
            </Field.Root>
          </SimpleGrid>

          <Field.Root required>
            <Field.Label fontWeight="600">Precio por Kilo</Field.Label>
            <InputGroup startElement={<Text color="gray.500">$</Text>}>
              <Input
                placeholder="0.00"
                type="number"
                min="0.01"
                step="0.01"
                bg="fondo.pagina"
                rounded="lg"
                value={precioPorKilo}
                onChange={(evento) => setPrecioPorKilo(evento.target.value)}
                required
              />
            </InputGroup>
            {estadoAnalisis === "valido" ? (
              <Text fontSize="sm" color="gray.500" mt={1}>
                Precio sugerido según el catálogo de mercado y el estado del material (editable).
              </Text>
            ) : (
              <Text fontSize="sm" color="gray.500" mt={1}>
                Sube la foto del material (más arriba) para recibir una sugerencia de precio.
              </Text>
            )}
          </Field.Root>
        </VStack>
      </TarjetaSeccion>

      <TarjetaSeccion titulo="Ubicación de Recogida">
        <VStack gap={5} align="stretch">
          <Field.Root>
            <Field.Label fontWeight="600">Barrio de referencia (opcional)</Field.Label>
            <SelectorDesplegable
              titulo="Seleccionar barrio"
              colecciondeDatos={barriosQuito}
              mostrarEtiqueta={false}
              valor={ubicacion ? valorBarrioMasCercano(ubicacion.latitud, ubicacion.longitud) : ""}
              alCambiar={(valorBarrio) => {
                const coordenadas = obtenerCoordenadasDeBarrio(valorBarrio);
                if (coordenadas) setUbicacion(coordenadas);
              }}
              iconoInicio={
                <Icono componente={<MdOutlineLocationOn />} tamanio="md" color="marca.primario" />
              }
            />
          </Field.Root>

          <Field.Root required>
            <Field.Label fontWeight="600">Punto exacto en el mapa</Field.Label>
            <SelectorUbicacionMapa
              valor={ubicacion}
              alCambiar={(latitud, longitud) => setUbicacion({ latitud, longitud })}
            />
          </Field.Root>
        </VStack>
      </TarjetaSeccion>

      <Flex
        justify="flex-end"
        gap={3}
        borderTop="1px solid"
        borderColor="gray.200"
        pt={5}
        align="center"
      >
        <Button
          variant="ghost"
          colorPalette="gray"
          rounded="lg"
          onClick={alCancelar}
          disabled={estaEnviando}
        >
          Cancelar
        </Button>
        <Button
          type="submit"
          colorPalette="verde"
          bg="marca.primario"
          rounded="lg"
          px={5}
          loading={estaEnviando}
          loadingText={modoEdicion ? "Guardando" : "Publicando"}
          disabled={estadoAnalisis === "analizando" || estadoAnalisis === "rechazado"}
        >
          <MdOutlineFileUpload /> {modoEdicion ? "Guardar Cambios" : "Publicar Material"}
        </Button>
      </Flex>
    </VStack>
  );
};

export default FormularioCrearPublicacion;
