import { Box } from "@chakra-ui/react";
import EncabezadoApp from "../organismos/EncabezadoApp";
import { useNavigate } from "react-router-dom";
import { fondoPagina, contenedorApp, paddingContenido } from "./estilosLayout";


const DiseniodeAplicacion = ({ titulo = "BarrioCircular", mostrarAtras = true, children }) => {
    const navigate = useNavigate();

    return (
        <Box {...fondoPagina}>
            <Box {...contenedorApp}>
                <EncabezadoApp
                    titulo={titulo}
                    mostrarAtras={mostrarAtras}
                    alPresionarAtras={() => navigate(-1)}
                    alPresionarMenu={() => {}}
                />
                <Box flex={1} overflowY="auto" {...paddingContenido}>
                    {children}
                </Box>
            </Box>
        </Box>
    );
};

export default DiseniodeAplicacion;
