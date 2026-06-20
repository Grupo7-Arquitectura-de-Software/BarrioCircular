import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelInicioRecolector from "../componentes/organismos/PanelInicioRecolector";

const PaginaInicioRecolector = () => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="Panel Reciclador" mostrarAtras={true}>
            <PanelInicioRecolector
                alVender={() => navigate("/recolector/vender/crear-publicacion")}
                alComprar={() => navigate("/recolector/ofertas-recomendadas")}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaInicioRecolector;
