import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormularioCrearPublicacion from "../componentes/organismos/FormularioCrearPublicacion";


const PaginaCrearPublicaciones = ({ prefijoRuta = "/ciudadano" }) => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
            <FormularioCrearPublicacion
                alPublicar={() => navigate(`${prefijoRuta}/publicacion-disponible`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaCrearPublicaciones;