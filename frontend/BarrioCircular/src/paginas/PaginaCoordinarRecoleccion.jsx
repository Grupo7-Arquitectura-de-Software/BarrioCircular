import { VStack, Text } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import DiseniodePanelControl from "../componentes/plantillas/DiseniodePanelControl.jsx";
import ChatdeCoordinacion from "../componentes/organismos/ChatdeCoordinacion";
import CalendarioCoordinacion from "../componentes/organismos/CalendarioCoordinacion";

const PaginaCoordinarRecoleccion = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();
  const rutaVerificar = `/${rol}/verificar/${id ?? "1"}`;

  if (rol === "recolector") {
    return (
      <DiseniodePanelControl titulo="Coordinar recolección" mostrarAtras={true}>
        <VStack gap={4} align="stretch">
          <Text fontSize="xs" color="gray.500" textAlign="center">
            Coordinar Recolección
          </Text>
          <ChatdeCoordinacion participante="Vendedor" />
          <CalendarioCoordinacion alConfirmarCita={() => navigate(rutaVerificar)} />
        </VStack>
      </DiseniodePanelControl>
    );
  }

  return (
    <DiseniodeAplicacion titulo="Coordinar recolección" mostrarAtras={true}>
      <CalendarioCoordinacion alConfirmarCita={() => navigate(rutaVerificar)} />
    </DiseniodeAplicacion>
  );
};

export default PaginaCoordinarRecoleccion;
