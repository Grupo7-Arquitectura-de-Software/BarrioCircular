import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "@/componentes/plantillas/DiseniodeAplicacion";
import FormulariodeVerificacion from "@/componentes/organismos/FormulariodeVerificacion";

const PaginaCentroValidacionMaterial = () => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion titulo="Verificar material recibido" mostrarAtras={true}>
      <FormulariodeVerificacion
        alConfirmar={() => navigate("/centro/confirmar-operacion")}
        alReportar={() => {}}
      />
    </DiseniodeAplicacion>
  );
};

export default PaginaCentroValidacionMaterial;
