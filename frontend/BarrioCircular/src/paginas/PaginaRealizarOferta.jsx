import { useNavigate, useParams } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import FormularioRealizarOferta from "../componentes/organismos/FormularioRealizarOferta";

const PaginaRealizarOferta = ({ rol = "recolector" }) => {
    const navigate = useNavigate();
    const { id } = useParams();

    return (
        <DiseniodeAplicacion titulo="Realizar oferta" mostrarAtras={true}>
            <FormularioRealizarOferta
                alEnviar={() => navigate(`/${rol}/espera/${id ?? "1"}`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaRealizarOferta;
