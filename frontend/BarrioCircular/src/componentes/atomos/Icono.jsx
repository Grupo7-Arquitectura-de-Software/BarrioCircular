import { Icon } from "@chakra-ui/react";


const Icono = ({ componente, tamanio = "md", color = "inherit" }) => {
    return (
        <Icon fontSize={tamanio} color={color}>
            {componente}
        </Icon>
    );
};

export default Icono;