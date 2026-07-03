import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion";
import PanelResultadoFinal from "../componentes/organismos/PanelResultadoFinal";
import Boton from "../componentes/atomos/Boton";

const PaginaResultadoOperacion = () => {
  const navigate = useNavigate();

  return (
    <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
      <PanelResultadoFinal
        tipoMaterial="Cartón"
        pesoKg={10}
        distancia="2km"
        observacion="$5.00"
        recolector="Reciclador Juan"
        resultado="completada"
      />
      <Boton
        texto="Volver al inicio"
        variante="outline"
        ancho="full"
        alHacer={() => navigate("/seleccionar-rol")}
      />
    </DiseniodeAplicacion>
  );
};

export default PaginaResultadoOperacion;
