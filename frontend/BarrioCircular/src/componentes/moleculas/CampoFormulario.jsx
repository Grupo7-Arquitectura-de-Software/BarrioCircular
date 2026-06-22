import CampoEntrada from "../atomos/CampoEntrada.jsx";
import {Field} from "@chakra-ui/react"


const CampoFormulario = ({etiqueta, marcadorPosicion}) => {
    return (
        <Field.Root>
            <Field.Label>{etiqueta}</Field.Label>
            <CampoEntrada placeholder={marcadorPosicion}/>
        </Field.Root>
    );
};

export default CampoFormulario;