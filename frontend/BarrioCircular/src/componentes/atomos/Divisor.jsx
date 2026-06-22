import { Separator } from "@chakra-ui/react";

/**
 * Átomo: Divisor / separador horizontal
 * @param {string} margen - Margen vertical (ej. "2" = 8px)
 */
const Divisor = ({ margen = "2" }) => {
    return <Separator my={margen} />;
};

export default Divisor;
