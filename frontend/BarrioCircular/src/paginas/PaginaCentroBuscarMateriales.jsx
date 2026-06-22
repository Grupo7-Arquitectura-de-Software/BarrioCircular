import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioBuscarMateriales from "../componentes/organismos/FormularioBuscarMateriales";

const PaginaCentroBuscarMateriales = () => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="Buscar materiales" mostrarAtras={true}>
            <FormularioBuscarMateriales
                alBuscar={() => navigate("/centro/ofertas-recomendadas")}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaCentroBuscarMateriales;
