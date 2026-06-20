import { Text } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import ListaPublicacionesRecomendadas from "../componentes/organismos/ListaPublicacionesRecomendadas";

const PaginaOfertasRecomendadas = ({ rol = "recolector" }) => {
    const navigate = useNavigate();
    const titulo = rol === "centro" ? "MATERIALES RECOMENDADOS" : "Ofertas recomendadas";

    return (
        <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
            <Text fontSize="md" fontWeight="bold" textAlign="center" mb={4}>
                {titulo}
            </Text>
            <ListaPublicacionesRecomendadas
                alSeleccionar={(id) => navigate(`/${rol}/detalle/${id}`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaOfertasRecomendadas;
