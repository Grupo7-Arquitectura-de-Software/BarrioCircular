import { Input, Textarea } from "@chakra-ui/react";

const CampoEntrada = ({
    marcadordePosicion = "",
    tipo = "text",
    valor,
    alCambiar,
    tamanio = "md",
    deshabilitado = false,
    filas = 3,
}) => {
    if (tipo === "textarea") {
        return (
            <Textarea
                placeholder={marcadordePosicion}
                value={valor}
                onChange={alCambiar}
                size={tamanio}
                disabled={deshabilitado}
                rows={filas}
                resize="none"
            />
        );
    }
    return (
        <Input
            type={tipo}
            placeholder={marcadordePosicion}
            value={valor}
            onChange={alCambiar}
            size={tamanio}
            disabled={deshabilitado}
        />
    );
};

export default CampoEntrada;