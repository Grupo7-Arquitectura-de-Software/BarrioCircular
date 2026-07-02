import { Button } from "@chakra-ui/react";

const Boton = ({
  texto,
  variante = "solid",
  colorEsquema = "gray",
  ancho = "auto",
  alHacer,
  icono,
  tamanio = "md",
  deshabilitado = false,
  tipo = "button",
}) => {
  return (
    <Button
      variant={variante}
      colorPalette={colorEsquema}
      width={ancho === "full" ? "100%" : "auto"}
      onClick={alHacer}
      size={tamanio}
      disabled={deshabilitado}
      type={tipo}
      rounded="md"
    >
      {icono && icono}
      {texto}
    </Button>
  );
};

export default Boton;
