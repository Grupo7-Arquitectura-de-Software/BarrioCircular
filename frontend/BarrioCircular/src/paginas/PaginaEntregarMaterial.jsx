import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioEntregaMaterial from "../componentes/organismos/FormularioEntregaMaterial";


const PaginaEntregarMaterial = ({ prefijoRuta = "/ciudadano" }) => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
            <FormularioEntregaMaterial
                alConfirmar={() => navigate(`${prefijoRuta}/resultado`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaEntregarMaterial;
