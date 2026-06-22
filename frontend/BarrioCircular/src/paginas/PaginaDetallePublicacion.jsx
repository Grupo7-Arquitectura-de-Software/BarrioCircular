import { useNavigate, useParams } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import DetallePublicacion from "../componentes/organismos/DetallePublicacion";

const PaginaDetallePublicacion = ({ rol = "recolector" }) => {
    const navigate = useNavigate();
    const { id } = useParams();

    return (
        <DiseniodeAplicacion titulo="Detalle de publicación" mostrarAtras={true}>
            <DetallePublicacion
                alRealizarOferta={() => navigate(`/${rol}/realizar-oferta/${id ?? "1"}`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaDetallePublicacion;
