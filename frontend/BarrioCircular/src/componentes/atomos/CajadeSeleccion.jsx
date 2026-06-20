import {Checkbox} from "@chakra-ui/react"

const CajadeSeleccion = ({Descripcion}) => {
    return (
        <Checkbox.Root>
            <Checkbox.HiddenInput/>
            <Checkbox.Control/>
            <Checkbox.Label>{Descripcion}</Checkbox.Label>
        </Checkbox.Root>
    );
};

export default CajadeSeleccion;