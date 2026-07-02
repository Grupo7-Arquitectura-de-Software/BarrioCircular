import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { VStack, Text } from "@chakra-ui/react";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import ListadeOfertas from "../componentes/organismos/ListadeOfertas";
import ModalOfertaAceptada from "../componentes/organismos/ModalOfertaAceptada";

const PaginadeReseniaOfertas = ({ prefijoRuta = "/ciudadano" }) => {
  const navigate = useNavigate();
  const [ofertaVista, setOfertaVista] = useState(null);

  const OFERTAS = [
    {
      id: 1,
      comprador: "Reciclador Juan",
      tipoComprador: "Reciclador",
      monto: "$4.50",
      distancia: "2km",
      observacion: "Recogida rápida",
    },
    {
      id: 2,
      comprador: "Centro de Recolección Norte",
      tipoComprador: "Centro de Recolección",
      monto: "$4.80",
      distancia: "5km",
      observacion: "Recogida rápida",
    },
  ];

  return (
    <DiseniodeAplicacion titulo="Ver ofertas recibidas" mostrarAtras={true}>
      <VStack gap={4} align="stretch" w="100%">
        <ListadeOfertas ofertas={OFERTAS} alVerDetalle={(id) => setOfertaVista(id)} />

        {/* Sección de revisión de ofertas */}
        <Text fontSize="sm" fontWeight="semibold" color="gray.600" mt={2}>
          Revisar ofertas:
        </Text>

        <VStack gap={3} align="stretch">
          {OFERTAS.map((o) => (
            <ModalOfertaAceptada
              key={o.id}
              nombreOfertante={o.comprador}
              tipoOfertante={o.tipoComprador}
              monto={o.monto}
              distancia={o.distancia}
              observacion={o.observacion}
              esSeleccionada={ofertaVista === o.id}
              alAceptar={() => navigate(`${prefijoRuta}/publicacion-reservada`)}
              alRechazar={() => setOfertaVista(null)}
            />
          ))}
        </VStack>
      </VStack>
    </DiseniodeAplicacion>
  );
};

export default PaginadeReseniaOfertas;
