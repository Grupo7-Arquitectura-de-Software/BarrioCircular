import {Box} from "@chakra-ui/react";
import EncabezadoApp from "../organismos/EncabezadoApp";
import {useNavigate} from "react-router-dom";
import {fondoPagina, contenedorApp, paddingContenido} from "./estilosLayout";
import {useCerrarSesion} from "@/utilidades/useCerrarSesion";


const DiseniodePanelControl = ({titulo = "BarrioCircular", mostrarAtras = true, children}) => {
    const navigate = useNavigate();
    const cerrarSesion = useCerrarSesion();

    return (
        <Box {...fondoPagina}>
            <Box {...contenedorApp} bg="gray.50">
                <EncabezadoApp
                    titulo={titulo}
                    mostrarAtras={mostrarAtras}
                    alPresionarAtras={() => navigate(-1)}
                    opcionesMenu={[
                        {valor: "cerrar-sesion", etiqueta: "Cerrar sesión", alSeleccionar: cerrarSesion},
                    ]}
                />
                <Box flex={1} overflowY="auto" {...paddingContenido} bg="white">
                    {children}
                </Box>
            </Box>
        </Box>
    );
};

export default DiseniodePanelControl;
