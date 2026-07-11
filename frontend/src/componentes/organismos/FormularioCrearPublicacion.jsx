import { useState } from "react";
import { useAuth } from "@clerk/clerk-react";
import {
  Box,
  Button,
  Field,
  Flex,
  Input,
  InputGroup,
  SimpleGrid,
  Text,
  VStack,
  createListCollection,
} from "@chakra-ui/react";
import { MdOutlineFileUpload, MdOutlineLocationOn, MdOutlineAutoAwesome } from "react-icons/md";
import SelectorDesplegable from "../atomos/SelectorDesplegable.jsx";
import AreaCargaImagenes from "../moleculas/AreaCargaImagenes.jsx";
import SelectorUbicacionMapa from "./SelectorUbicacionMapa.jsx";
import Icono from "../atomos/Icono.jsx";
import { toaster } from "@/components/ui/toaster-instance";
import {
  BARRIOS_QUITO,
  ETIQUETAS_TIPO_RESIDUO,
  obtenerCoordenadasDeBarrio,
  valorBarrioMasCercano,
} from "@/utilidades/barriosQuito";
import { sugerirPrecio } from "@/servicios/sugerenciaPrecioService";

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
 * Formulario "Nueva Publicación": recolecta los datos alineados al contrato
 * del backend (POST /api/publicaciones) y los entrega vía `alPublicar`.
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
  const [estaSugiriendoPrecio, setEstaSugiriendoPrecio] = useState(false);
  const [justificacionSugerida, setJustificacionSugerida] = useState("");

  const advertir = (titulo, descripcion) => {
    toaster.create({ title: titulo, description: descripcion, type: "warning", duration: 3500 });
  };

  const sugerirPrecioConIA = async () => {
    if (!tipoResiduo) {
      advertir(
        "Selecciona el tipo de material",
        "Elige una categoría antes de pedir una sugerencia.",
      );
      return;
    }
    if (!archivoEvidencia) {
      advertir(
        "Sube la foto de evidencia",
        "La IA necesita ver el material para sugerir un precio ajustado a su estado.",
      );
      return;
    }

    setEstaSugiriendoPrecio(true);
    try {
      const [token, imagenBase64] = await Promise.all([
        getToken(),
        convertirArchivoABase64(archivoEvidencia),
      ]);
      const resultado = await sugerirPrecio(token, {
        tipoResiduo,
        pesoKg: pesoKg ? Number(pesoKg) : null,
        imagenBase64,
      });
      setPrecioPorKilo(String(resultado.precioSugeridoPorKilo));
      setJustificacionSugerida(resultado.justificacion || "");
    } catch {
      advertir(
        "No se pudo sugerir un precio",
        "Intenta nuevamente o ingresa el precio manualmente.",
      );
    } finally {
      setEstaSugiriendoPrecio(false);
    }
  };

  const enviarFormulario = (evento) => {
    evento.preventDefault();

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
    if (!modoEdicion && !archivoEvidencia) {
      advertir("Falta la foto de evidencia", "Sube una foto del material para publicar.");
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

          <Field.Root required={!modoEdicion}>
            <Field.Label fontWeight="600">Foto de Evidencia</Field.Label>
            {modoEdicion && (
              <Text fontSize="sm" color="gray.500" mb={2}>
                Ya tienes una foto subida. Sube una nueva solo si quieres reemplazarla.
              </Text>
            )}
            <AreaCargaImagenes
              maximoArchivos={1}
              tamanioMaximoMB={10}
              alCambiarArchivos={({ acceptedFiles }) => {
                setArchivoEvidencia(acceptedFiles[0] ?? null);
                setJustificacionSugerida("");
              }}
            />
          </Field.Root>

          <Field.Root required>
            <Field.Label fontWeight="600">Precio por Kilo</Field.Label>
            <Flex gap={2} w="100%">
              <InputGroup startElement={<Text color="gray.500">$</Text>} flex="1">
                <Input
                  placeholder="0.00"
                  type="number"
                  min="0.01"
                  step="0.01"
                  bg="fondo.pagina"
                  rounded="lg"
                  value={precioPorKilo}
                  onChange={(evento) => {
                    setPrecioPorKilo(evento.target.value);
                    setJustificacionSugerida("");
                  }}
                  required
                />
              </InputGroup>
              <Button
                type="button"
                variant="outline"
                colorPalette="verde"
                rounded="lg"
                flexShrink={0}
                onClick={sugerirPrecioConIA}
                loading={estaSugiriendoPrecio}
                loadingText="Sugiriendo"
                disabled={!tipoResiduo || !archivoEvidencia}
              >
                <MdOutlineAutoAwesome /> Sugerir precio con IA
              </Button>
            </Flex>
            {!archivoEvidencia && (
              <Text fontSize="sm" color="gray.500" mt={1}>
                Sube la foto de evidencia (más arriba) para habilitar la sugerencia con IA.
              </Text>
            )}
            {justificacionSugerida && (
              <Text fontSize="sm" color="gray.500" mt={1}>
                Sugerencia de IA: {justificacionSugerida}
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
        >
          <MdOutlineFileUpload /> {modoEdicion ? "Guardar Cambios" : "Publicar Material"}
        </Button>
      </Flex>
    </VStack>
  );
};

export default FormularioCrearPublicacion;
