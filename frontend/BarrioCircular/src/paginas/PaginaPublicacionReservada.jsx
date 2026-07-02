import { VStack, Box, Text, HStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import AvatarUsuario from "../componentes/atomos/AvatarUsuario";
import EtiquetaInformacion from "../componentes/moleculas/EtiquetaInformacion";
import Divisor from "../componentes/atomos/Divisor";
import Boton from "../componentes/atomos/Boton";

const PaginaPublicacionReservada = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
      <VStack gap={4} align="stretch" w="100%">
        <Box p={3} border="2px solid" borderColor="gray.700" borderRadius="md" bg="gray.50">
          <VStack gap={2} align="stretch">
            <Text fontSize="xs" fontWeight="bold" color="gray.500" textAlign="center">
              Aceptar mejor oferta
            </Text>
            <AvatarUsuario nombre="Reciclador Juan" tipo="Reciclador" tamanio="sm" />
            <EtiquetaInformacion etiqueta="Monto:" valor="$4.80" />
            <EtiquetaInformacion etiqueta="Monto:" valor="$4.80" />
            <EtiquetaInformacion etiqueta="Distancia:" valor="2km" />
            <EtiquetaInformacion etiqueta="Observación:" valor="Recogida rápida" />
            <VStack gap={2}>
              <Boton
                texto="Aceptar"
                variante="solid"
                colorEsquema="gray"
                ancho="full"
                tamanio="sm"
                alHacer={() => navigate(`${prefijoRuta}/coordinar`)}
              />
              <Boton
                texto="Rechazar"
                variante="outline"
                colorEsquema="red"
                ancho="full"
                tamanio="sm"
              />
            </VStack>
            <HStack justify="center" gap={1} color="green.500">
              <Text fontSize="xs" color="green.600">
                ✓ Oferta aceptada
              </Text>
            </HStack>
          </VStack>
        </Box>

        <Divisor />

        {/* Datos de publicación reservada */}
        <AvatarUsuario nombre="Reciclador Juan" tipo="Reciclador" tamanio="md" />

        <VStack gap={1} align="stretch">
          <EtiquetaInformacion etiqueta="Estado:" valor="Reservado" />
          <EtiquetaInformacion etiqueta="Contacto:" valor="Reciclador Juan" />
          <EtiquetaInformacion etiqueta="Teléfono:" valor="+1123 356-6373" />
          <EtiquetaInformacion etiqueta="Email:" valor="mia@recolector.com" />
        </VStack>

        <Divisor />

        <VStack gap={1} align="stretch">
          <Text fontSize="sm" fontWeight="semibold">
            Material:
          </Text>
          <EtiquetaInformacion etiqueta="Tipo:" valor="Cartón, 10kg" />
          <EtiquetaInformacion etiqueta="Peso:" valor="$5.00" />
          <EtiquetaInformacion etiqueta="Precio ref.:" valor="$5.00" />
          <EtiquetaInformacion etiqueta="Ubicación:" valor="La Mariscal" />
        </VStack>

        <Boton
          texto="Coordinar Recolección"
          variante="solid"
          colorEsquema="gray"
          ancho="full"
          alHacer={() => navigate(`${prefijoRuta}/coordinar`)}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaPublicacionReservada;
