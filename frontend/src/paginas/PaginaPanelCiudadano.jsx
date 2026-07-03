import { Button, Flex, SimpleGrid, Text, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { MdOutlineFilterList, MdOutlineInventory2, MdOutlineRecycling } from "react-icons/md";
import { LuLeaf } from "react-icons/lu";

import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaEstadistica from "../componentes/moleculas/TarjetaEstadistica.jsx";
import TarjetaPublicacion from "../componentes/organismos/TarjetaPublicacion.jsx";
import {
  NAVEGACION_CIUDADANO,
  RUTA_NUEVA_PUBLICACION_CIUDADANO,
} from "@/utilidades/navegacionPanel";

// Datos de ejemplo alineados al mockup "Mis Publicaciones" (Entregable 3).
const PUBLICACIONES_RECIENTES = [
  {
    id: 1,
    titulo: "Botellas de Plástico PET",
    descripcion:
      "Botellas de PET transparentes, limpias y aplastadas, recolectadas durante dos semanas.",
    pesoKg: 15,
    ubicacion: "La Floresta",
    estado: "Disponible",
  },
  {
    id: 2,
    titulo: "Cartón Corrugado",
    descripcion: "Cajas de cartón corrugado aplanadas y secas de entregas recientes.",
    pesoKg: 42,
    ubicacion: "Cumbayá",
    estado: "Recolección Pendiente",
  },
  {
    id: 3,
    titulo: "Frascos de Vidrio Mixtos",
    descripcion:
      "Colores surtidos, necesita mejor clasificación antes de que se apruebe el anuncio.",
    estado: "Acción Requerida",
    etiquetaAccion: "Actualizar Detalles",
  },
];

const PaginaPanelCiudadano = () => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion
      navegacion={NAVEGACION_CIUDADANO}
      rutaNuevaPublicacion={RUTA_NUEVA_PUBLICACION_CIUDADANO}
    >
      <VStack align="stretch" gap={8}>
        <VStack align="stretch" gap={1}>
          <Text fontFamily="heading" fontWeight="700" fontSize={{ base: "2xl", md: "3xl" }}>
            Mi Panel
          </Text>
          <Text color="gray.600">Rastrea tu impacto regenerativo y anuncios activos.</Text>
        </VStack>

        {/* Estadísticas */}
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={5}>
          <TarjetaEstadistica
            icono={<MdOutlineRecycling />}
            etiqueta="Total Reciclado"
            valor="245"
            unidad="kg"
            acento="verde"
            insignia="+12% este mes"
          />
          <TarjetaEstadistica
            icono={<LuLeaf />}
            etiqueta="Compensación de CO2"
            valor="1.2"
            unidad="toneladas"
            acento="azul"
          />
          <TarjetaEstadistica
            icono={<MdOutlineInventory2 />}
            etiqueta="Anuncios Activos"
            valor="8"
            acento="neutro"
            etiquetaAccion="Ver todo"
            alAccionar={() => navigate("/ciudadano/publicacion-disponible")}
          />
        </SimpleGrid>

        {/* Publicaciones recientes */}
        <VStack align="stretch" gap={4}>
          <Flex justify="space-between" align="center">
            <Text fontFamily="heading" fontWeight="700" fontSize="xl">
              Publicaciones Recientes
            </Text>
            <Button variant="outline" size="sm" rounded="lg" colorPalette="gray">
              <MdOutlineFilterList /> Filtrar
            </Button>
          </Flex>

          <SimpleGrid columns={{ base: 1, md: 2, xl: 3 }} gap={5}>
            {PUBLICACIONES_RECIENTES.map((publicacion) => (
              <TarjetaPublicacion
                key={publicacion.id}
                titulo={publicacion.titulo}
                descripcion={publicacion.descripcion}
                pesoKg={publicacion.pesoKg}
                ubicacion={publicacion.ubicacion}
                estado={publicacion.estado}
                etiquetaAccion={publicacion.etiquetaAccion}
                alAccionar={() => navigate("/ciudadano/crear-publicacion")}
                alHacerClick={() => navigate("/ciudadano/publicacion-disponible")}
              />
            ))}
          </SimpleGrid>
        </VStack>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPanelCiudadano;
