import { HStack, Image, Text, VStack } from "@chakra-ui/react";
import logoBarrioCircular from "@/assets/LogoBarrioCircular.png";

const TAMANIOS = {
  sm: { logo: "36px", fuente: "xl" },
  md: { logo: "48px", fuente: "2xl" },
  lg: { logo: "64px", fuente: "3xl" },
};

const LogotipoApp = ({ tamanio = "md", orientacion = "vertical", mostrarNombre = true }) => {
  const s = TAMANIOS[tamanio] || TAMANIOS.md;
  const Contenedor = orientacion === "horizontal" ? HStack : VStack;

  return (
    <Contenedor gap={2} align="center" justify="center">
      <Image
        src={logoBarrioCircular}
        alt="Logotipo de BarrioCircular"
        boxSize={s.logo}
        fit="contain"
      />
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
