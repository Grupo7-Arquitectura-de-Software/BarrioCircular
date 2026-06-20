import {Text} from "@chakra-ui/react"

const Texto = ({texto, volumendeFuente, tamanio}) => {
    return (
        <Text size={tamanio} fontWeight={volumendeFuente}>{texto}</Text>
    );
};

export default Texto;