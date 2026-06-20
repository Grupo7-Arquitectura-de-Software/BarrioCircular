import { Navigate, useNavigate, useParams } from "react-router-dom";
import DiseniodeAutenticacion from "../componentes/plantillas/DiseniodeAutenticacion.jsx";
import FormularioAutenticacion from "../componentes/organismos/FormularioAutenticacion";
import { ETIQUETAS_ROL, RUTAS_INICIO_ROL } from "../componentes/plantillas/estilosLayout";

const ROLES_VALIDOS = ["ciudadano", "recolector", "centro"];

const PaginaAutenticacion = () => {
    const { rol } = useParams();
    const navigate = useNavigate();

    if (!ROLES_VALIDOS.includes(rol)) {
        return <Navigate to="/seleccionar-rol" replace />;
    }

    return (
        <DiseniodeAutenticacion>
            <FormularioAutenticacion
                rol={rol}
                etiquetaRol={ETIQUETAS_ROL[rol]}
                alContinuar={() => navigate(RUTAS_INICIO_ROL[rol])}
                alVolver={() => navigate("/seleccionar-rol")}
            />
        </DiseniodeAutenticacion>
    );
};

export default PaginaAutenticacion;
