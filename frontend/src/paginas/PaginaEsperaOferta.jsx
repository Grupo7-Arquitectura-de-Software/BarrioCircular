import { VStack } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelEstadoOferta from "../componentes/moleculas/PanelEstadoOferta";
import Boton from "../componentes/atomos/Boton";

const PaginaEsperaOferta = ({ rol = "recolector" }) => {
  const navigate = useNavigate();
  const { id } = useParams();

  return (
    <DiseniodeAplicacion titulo="Oferta enviada" mostrarAtras={true}>
      <VStack gap={4} align="stretch">
        <PanelEstadoOferta
          titulo="Oferta Pendiente"
          subtitulo="Notificación cuando acepten"
          estadoTexto="Pendiente"
        />
        <Boton
          texto="Simular oferta aceptada"
          variante="solid"
          colorEsquema="gray"
          ancho="full"
          alHacer={() => navigate(`/${rol}/coordinar/${id ?? "1"}`)}
        />
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginaEsperaOferta;
