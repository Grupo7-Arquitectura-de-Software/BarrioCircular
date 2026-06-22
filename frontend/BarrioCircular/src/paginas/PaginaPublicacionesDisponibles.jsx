import { useNavigate } from "react-router-dom";
import DiseniodeAplicacion from "../componentes/plantillas/DiseniodeAplicacion.jsx";
import TarjetaPublicacion from "../componentes/organismos/TarjetaPublicacion";


const PaginaPublicacionesDisponibles = ({ prefijoRuta = "/ciudadano" }) => {
    const navigate = useNavigate();

    return (
        <DiseniodeAplicacion titulo="BarrioCircular" mostrarAtras={true}>
            <TarjetaPublicacion
                tipoMaterial="Cartón"
                pesoKg={10}
                precio="$5.00"
                ubicacion="La Mariscal"
                estado="Disponible"
                cantidadOfertas={0}
                alVerOfertas={() => navigate(`${prefijoRuta}/ver-ofertas`)}
            />
        </DiseniodeAplicacion>
    );
};

export default PaginaPublicacionesDisponibles;