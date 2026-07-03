import { HStack } from "@chakra-ui/react";
import SelectorDesplegable from "../atomos/SelectorDesplegable";
import { createListCollection } from "@chakra-ui/react";

const FiltrodeMaterial = () => {
  const tiposMaterial = createListCollection({
    items: [
      { label: "Cartón", value: "carton" },
      { label: "Plástico PET", value: "plastico_pet" },
      { label: "Vidrio", value: "vidrio" },
      { label: "Metal", value: "metal" },
      { label: "Papel", value: "papel" },
      { label: "Madera", value: "madera" },
    ],
  });

  const cantidades = createListCollection({
    items: [
      { label: "50-100 kg", value: "50-100" },
      { label: "100-250 kg", value: "100-250" },
      { label: "250-500 kg", value: "250-500" },
      { label: "500+ kg", value: "500+" },
    ],
  });

  const precios = createListCollection({
    items: [
      { label: "$0 - $0.10/kg", value: "0-0.10" },
      { label: "$0.10 - $0.50/kg", value: "0.10-0.50" },
      { label: "$0.50 - $1.00/kg", value: "0.50-1.00" },
      { label: "$1.00+/kg", value: "1.00+" },
    ],
  });

  return (
    <HStack gap={3} flexWrap="wrap">
      <SelectorDesplegable titulo="Tipo Material" colecciondeDatos={tiposMaterial} />
      <SelectorDesplegable titulo="Cantidad Aprox. (kg)" colecciondeDatos={cantidades} />
      <SelectorDesplegable titulo="Precio/Kg (Suministro)" colecciondeDatos={precios} />
    </HStack>
  );
};

export default FiltrodeMaterial;
