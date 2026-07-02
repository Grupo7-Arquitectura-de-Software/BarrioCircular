import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import FormulariodeVerificacion from "../componentes/organismos/FormulariodeVerificacion";

const PaginadeValidacionMaterial = () => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
      <FormulariodeVerificacion
        alConfirmar={() => navigate("/recolector/confirmar-operacion")}
        alReportar={() => {}}
      />
    </DiseniodeAplicacion>
  );
};

export default PaginadeValidacionMaterial;
