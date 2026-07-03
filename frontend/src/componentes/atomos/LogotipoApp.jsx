import { HStack, Text, VStack } from "@chakra-ui/react";
import { LuLeaf } from "react-icons/lu";
import Icono from "./Icono.jsx";

const TAMANIOS = {
  sm: { icono: "2xl", fuente: "xl" },
  md: { icono: "3xl", fuente: "2xl" },
  lg: { icono: "4xl", fuente: "3xl" },
};

const LogotipoApp = ({ tamanio = "md", orientacion = "vertical", mostrarNombre = true }) => {
  const s = TAMANIOS[tamanio] || TAMANIOS.md;
  const Contenedor = orientacion === "horizontal" ? HStack : VStack;

  return (
    <Contenedor gap={2} align="center" justify="center">
      <Icono componente={<LuLeaf />} tamanio={s.icono} color="marca.primario" />
      {mostrarNombre && (
        <Text
          fontFamily="heading"
          fontWeight="700"
          fontSize={s.fuente}
          color="marca.primario"
          lineHeight="1"
        >
          BarrioCircular
        </Text>
      )}
    </Contenedor>
  );
};

export default LogotipoApp;
